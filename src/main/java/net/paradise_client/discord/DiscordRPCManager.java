package net.paradise_client.discord;

import net.arikia.dev.drpc.*;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;
import net.paradise_client.*;

import java.util.concurrent.*;

/**
 * Manages the Discord Rich Presence integration for the ParadiseClient mod.
 * <p>
 * This class initializes the Discord RPC, updates the rich presence status based on the game state, and handles
 * enabling/disabling the integration.
 * </p>
 *
 * @author 1nstagram
 */
public class DiscordRPCManager implements ReadyCallback {
  private final Minecraft client;
  private final RichPresenceUpdater richPresenceUpdater;
  private boolean enabled = true;
  private ScheduledExecutorService executorService;
  private GameState.State lastGameState;
  private boolean wasInGame = false;
  private boolean wasPaused = false;

  public DiscordRPCManager(Minecraft client) {
    this.client = client;
    this.richPresenceUpdater = new RichPresenceUpdater();
    this.lastGameState = null;
    init();
    startTask();
  }

  private void init() {
    DiscordEventHandlers handlers =
      new DiscordEventHandlers.Builder().setReadyEventHandler(user -> Constants.LOGGER.info(
        "Connected to Discord: {}#{} ({})",
        user.username,
        user.discriminator,
        user.userId)).build();
    try {
      DiscordRPC.discordInitialize("1164104022265974784", handlers, true);
    } catch (Exception e) {
      Constants.LOGGER.error("[DiscordRPC] Failed to initialize Discord RPC: ", e);
      enabled = false;
    }
  }

  public void startTask() {
    if (executorService != null && !executorService.isShutdown()) {
      executorService.shutdown();
    }

    executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleWithFixedDelay(this::updateDiscordPresence, 1, // Start after 1 second
      5, // Update every 5 seconds for more responsive updates
      TimeUnit.SECONDS);
  }

  private void updateDiscordPresence() {
    if (!enabled) {
      return;
    }

    try {
      // Get current game state information
      GameState.State currentState = GameState.getGameStateEnum(client);
      boolean isInGame = GameState.isInGame(client);
      boolean isPaused = GameState.isPaused(client);

      // Check if state has changed to avoid unnecessary updates
      boolean stateChanged = currentState != lastGameState || isInGame != wasInGame || isPaused != wasPaused;

      if (stateChanged) {
        String playerName = ParadiseClient.BUNGEE_SPOOF_MOD.usernameFake;
        String detailedState = GameState.getDetailedGameState(client);

        // Update presence with detailed information
        richPresenceUpdater.updatePresence(playerName, detailedState, isInGame, isPaused, currentState);

        // Update cached values
        lastGameState = currentState;
        wasInGame = isInGame;
        wasPaused = isPaused;

        Constants.LOGGER.info("[DiscordRPC] State changed to: {} (InGame: {}, Paused: {})",
          detailedState,
          isInGame,
          isPaused);
      }
    } catch (Exception e) {
      Constants.LOGGER.error("[DiscordRPC] Error updating presence: ", e);
    }
  }

  @Override public void apply(DiscordUser discordUser) {
    Constants.LOGGER.info("DiscordRPC Launched for user: " + discordUser.username);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }

    this.enabled = enabled;
    if (enabled) {
      init();
      startTask();
    } else {
      shutdown();
    }
  }

  public void shutdown() {
    if (executorService != null && !executorService.isShutdown()) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
          executorService.shutdownNow();
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
    DiscordRPC.discordShutdown();
  }
}