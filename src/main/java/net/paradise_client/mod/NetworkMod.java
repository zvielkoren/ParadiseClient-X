package net.paradise_client.mod;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.paradise_client.inject.accessor.PayloadTypeRegistryImplAccessor;

import java.util.ArrayList;

/**
 * Manages network connection state and server information.
 * <p>
 * This class tracks whether the client is connected to a server and stores the server's IP address.
 * </p>
 *
 * @author SpigotRCE
 * @since 2.17
 */
@SuppressWarnings("unused") public class NetworkMod {
  /**
   * Indicates whether the client is currently connected to a server.
   */
  public boolean isConnected = false;

  /**
   * The registered channels by their names.
   */
  public ArrayList<String> getRegisteredChannelsByName() {
    return ((PayloadTypeRegistryImplAccessor) PayloadTypeRegistry.serverboundPlay()).paradiseClient$getRegisteredChannelsByName();
  }
}
