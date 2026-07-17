package net.paradise_client.inject.mixin.network.handler;

import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
import net.paradise_client.event.bus.EventBus;
import net.paradise_client.event.impl.network.LoginEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHandshakePacketListenerImpl.class) public class ClientLoginNetworkHandlerMixin {
  @Inject(method = "handleLoginFinished", at = @At("HEAD"), cancellable = true)
  public void onSucess(ClientboundLoginFinishedPacket packet, CallbackInfo ci) {
    EventBus.fire(EventBus.LOGIN_EVENT_CHANNEL, new LoginEvent(packet.gameProfile()));
  }
}
