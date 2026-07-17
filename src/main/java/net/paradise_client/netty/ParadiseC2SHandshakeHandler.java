package net.paradise_client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.minecraft.network.FriendlyByteBuf;
import net.paradise_client.*;
import net.paradise_client.protocol.ProtocolVersion;
import net.paradise_client.protocol.packet.impl.HandshakePacket;

import java.util.List;

public class ParadiseC2SHandshakeHandler extends MessageToMessageEncoder<ByteBuf> {
  @Override protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    FriendlyByteBuf b = Helper.byteBufToPacketBuf(ctx.alloc().buffer().writeBytes(in));

    b.readVarInt();
    decodeHandshake(b);

    ctx.pipeline().remove(this);

    out.add(in.resetReaderIndex().retain());
  }

  private void decodeHandshake(FriendlyByteBuf b) {
    HandshakePacket handshake = new HandshakePacket();
    handshake.read(b.asByteBuf());
    int protocolVersion = handshake.getProtocolVersion();
    if (!(ProtocolVersion.isSupported(protocolVersion))) {
      throw new IllegalArgumentException("Protocol version: " +
        protocolVersion +
        " is not supported by ParadiseClient!");
    }
    ParadiseClient.NETWORK_CONFIGURATION.protocolVersion = protocolVersion;
  }
}
