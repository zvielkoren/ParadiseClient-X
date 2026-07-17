package net.paradise_client.inject.mixin.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.ProtocolSwapHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.paradise_client.Helper;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(PacketDecoder.class) public class DecoderHandlerMixin<T extends PacketListener> {
  @Mutable @Final @Shadow private final ProtocolInfo<T> protocolInfo;

  @SuppressWarnings("unused") public DecoderHandlerMixin(ProtocolInfo<T> state) {
    this.protocolInfo = state;
  }

  /**
   * Prevents issues with via version wrongly translating packets. Notifies in chat about handling errors.
   *
   * @author SpigotRCE
   * @reason To prevent disconnection issues
   */
  @Overwrite() public void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> objects) {
    int i = buf.readableBytes();
    if (i != 0) {
      Packet<? super T> packet = this.protocolInfo.codec().decode(buf);
      PacketType<? extends Packet<? super T>> packetType = packet.type();
      JvmProfiler.INSTANCE.onPacketReceived(this.protocolInfo.id(), packetType, context.channel().remoteAddress(), i);
      if (buf.readableBytes() > 0) {
        Helper.printChatMessage("&cError handling packet " +
          this.protocolInfo.id().id() +
          "/" +
          packetType +
          " (" +
          packet.getClass().getSimpleName() +
          ") was larger than I expected, found " +
          buf.readableBytes() +
          " bytes extra whilst reading packet " +
          packetType);
        Helper.printChatMessage("&cThis is a warning, not an error. The client would've disconnect if this" +
          "wasn't in place!");
      } else {
        objects.add(packet);
        ProtocolSwapHandler.handleInboundTerminalPacket(context, packet);
      }
    }
  }
}
