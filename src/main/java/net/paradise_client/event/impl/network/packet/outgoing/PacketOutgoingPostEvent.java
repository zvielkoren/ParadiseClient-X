package net.paradise_client.event.impl.network.packet.outgoing;

import net.minecraft.network.protocol.Packet;

public record PacketOutgoingPostEvent(Packet<?> packet) {
  public PacketOutgoingPostEvent() {
    this(null);
  }
}
