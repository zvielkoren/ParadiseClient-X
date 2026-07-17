package net.paradise_client.event.impl.network.packet.incoming;

import net.minecraft.network.protocol.Packet;

@SuppressWarnings("unused") public class PacketIncomingPreEvent {
  private Packet<?> packet;

  public PacketIncomingPreEvent() {
    this(null);
  }

  public PacketIncomingPreEvent(Packet<?> packet) {
    this.packet = packet;
  }

  public Packet<?> getPacket() {
    return packet;
  }

  public void setPacket(Packet<?> packet) {
    this.packet = packet;
  }
}
