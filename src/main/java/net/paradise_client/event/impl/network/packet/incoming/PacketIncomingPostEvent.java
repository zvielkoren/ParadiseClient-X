package net.paradise_client.event.impl.network.packet.incoming;

import net.minecraft.network.protocol.Packet;

public record PacketIncomingPostEvent(Packet<?> packet) {
  public PacketIncomingPostEvent() {
    this(null);
  }
}
