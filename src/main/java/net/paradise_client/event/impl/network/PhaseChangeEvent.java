package net.paradise_client.event.impl.network;

import net.minecraft.network.ConnectionProtocol;

/**
 * This event is fired when the network phase is changed.
 */
public record PhaseChangeEvent(ConnectionProtocol phase) {
  public PhaseChangeEvent() {
    this(null);
  }
}
