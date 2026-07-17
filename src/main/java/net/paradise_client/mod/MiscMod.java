package net.paradise_client.mod;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.chat.Component;

/**
 * Manages miscellaneous data related to network packets and screen state.
 * <p>
 * This class keeps track of various network packet details and the current screen being displayed.
 * </p>
 *
 * @author SpigotRCE
 * @since 1.1
 */
public class MiscMod {
  /**
   * Message which needs to be delayed before sending.
   */
  public final Queue<Component> delayedMessages = new ConcurrentLinkedQueue<>();

  /**
   * Weather the client is outdated or not.
   */
  public boolean isClientOutdated = false;

  /**
   * Latest version retrieved from the API.
   */
  public String latestVersion;

  /**
   * Command suggestions request id.
   */
  public int requestId;

  public boolean isDumping = false;
}
