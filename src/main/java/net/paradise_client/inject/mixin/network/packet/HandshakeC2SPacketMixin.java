package net.paradise_client.inject.mixin.network.packet;

import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.paradise_client.ParadiseClient;
import net.paradise_client.mod.BungeeSpoofMod;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin class to modify the behavior of the HandshakeC2SPacket class.
 * <p>
 * This class handles packet initialization and modifies the address and additional information based on BungeeCord
 * spoofing settings.
 * </p>
 *
 * @author SpigotRCE
 * @since 1.0
 */
@Mixin(ClientIntentionPacket.class) public class HandshakeC2SPacketMixin {

  @Mutable @Shadow @Final private String hostName;

  /**
   * Injects code into the constructor of the HandshakeC2SPacket class to modify the address and append BungeeCord
   * information if spoofing is enabled.
   * <p>
   * This method sets the address to the BungeeCord target IP and appends BungeeCord information if BungeeCord spoofing
   * is enabled and the connection intent is LOGIN.
   * </p>
   *
   * @param i                The first parameter of the constructor.
   * @param string           The address string.
   * @param j                The second parameter of the constructor.
   * @param connectionIntent The connection intent of the handshake.
   * @param ci               The callback information.
   */
  @Inject(method = "<init>(ILjava/lang/String;ILnet/minecraft/network/protocol/handshake/ClientIntent;)V",
    at = @At("RETURN")) private void HandshakeC2SPacket(int i,
    String string,
    int j,
    ClientIntent connectionIntent,
    CallbackInfo ci) {
    BungeeSpoofMod bungeeSpoofMod = ParadiseClient.BUNGEE_SPOOF_MOD;

    if (bungeeSpoofMod.isHostnameForwarding) {
      this.hostName = bungeeSpoofMod.hostname;
    }
    if (bungeeSpoofMod.isIPForwarding && connectionIntent == ClientIntent.LOGIN) {
      this.hostName += "\000" +
        bungeeSpoofMod.ip +
        "\000" +
        bungeeSpoofMod.uuid.toString().replace("-", "") +
        "\000" +
        "[{\"name\": \"bungeeguard-token\", \"value\": \"" +
        ParadiseClient.BUNGEE_SPOOF_MOD.token +
        "\"}]";
    }
  }
}
