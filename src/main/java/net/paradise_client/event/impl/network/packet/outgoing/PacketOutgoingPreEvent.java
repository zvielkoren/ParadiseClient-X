package net.paradise_client.event.impl.network.packet.outgoing;

import net.minecraft.network.protocol.Packet;

@SuppressWarnings("unused") public class PacketOutgoingPreEvent {
  private Packet<?> packet;

  public PacketOutgoingPreEvent() {
    this(null);
  }

  public PacketOutgoingPreEvent(Packet<?> packet) {
    this.packet = packet;
  }

  public Packet<?> getPacket() {
    return packet;
  }

  public void setPacket(Packet<?> packet) {
    this.packet = packet;
  }
}
