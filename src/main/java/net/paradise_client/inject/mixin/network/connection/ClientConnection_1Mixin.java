package net.paradise_client.inject.mixin.network.connection;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.*;
import net.minecraft.network.protocol.PacketFlow;
import net.paradise_client.Constants;
import net.paradise_client.netty.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Connection.class, priority = 1001) public class ClientConnection_1Mixin {
  /**
   * Responsible for injecting our handlers into the pipeline to decode packets before via version does.
   */
  @Inject(method = "configureSerialization", at = @At("RETURN")) private static void onAddHandlers(ChannelPipeline pipeline,
    PacketFlow side,
    boolean local,
    BandwidthDebugMonitor packetSizeLogger,
    CallbackInfo ci) {
    if (pipeline.channel() instanceof SocketChannel) {
      pipeline.addBefore(HandlerNames.ENCODER,
        NettyConstants.PARADISE_C2S_HANDSHAKE_HANDLER,
        new ParadiseC2SHandshakeHandler());
      pipeline.addBefore(HandlerNames.ENCODER, NettyConstants.PARADISE_ENCODER, new ParadiseEncoder());
      pipeline.addBefore(HandlerNames.INBOUND_CONFIG,
        NettyConstants.PARADISE_S2C_PLUGIN_MESSAGE_HANDLER,
        new ParadiseS2CPluginMessageHandler());
    } else {
      Constants.LOGGER.warn("Channel not an instance of netty socket");
    }
  }
}
