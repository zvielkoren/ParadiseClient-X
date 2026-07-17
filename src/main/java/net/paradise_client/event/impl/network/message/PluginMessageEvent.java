package net.paradise_client.event.impl.network.message;

import net.minecraft.network.FriendlyByteBuf;

/**
 * Event for a plugin message received from the server.
 */
public record PluginMessageEvent(String channel, FriendlyByteBuf buf) {
  public PluginMessageEvent() {
    this(null, null);
  }
}
