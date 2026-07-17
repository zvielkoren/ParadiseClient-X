package net.paradise_client.inject.mixin.etc;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.paradise_client.ParadiseClient;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class) public abstract class ClientPlayerEntityMixin {
  @Inject(method = "tick", at = @At("TAIL")) public void tick(CallbackInfo ci) {
    Component msg;
    while ((msg = ParadiseClient.MISC_MOD.delayedMessages.poll()) != null) {
      this.displayClientMessage(msg, false);
    }
  }

  @Shadow public abstract void displayClientMessage(Component message, boolean overlay);

  @Unique private void displayClientMessage(Component message) {
    this.displayClientMessage(message, false);
  }
}
