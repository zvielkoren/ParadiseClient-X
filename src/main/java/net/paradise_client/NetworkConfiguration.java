package net.paradise_client;

import net.minecraft.network.*;
import net.minecraft.network.protocol.PacketFlow;

public class NetworkConfiguration {
  public ConnectionProtocol phase;
  public PacketFlow side;
  public int protocolVersion;
  public long lastPacket;

  public void set(ConnectionProtocol phase, PacketFlow side, int protocolVersion) {
    this.phase = phase;
    this.side = side;
    this.protocolVersion = protocolVersion;
  }
}
