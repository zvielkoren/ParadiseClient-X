package net.paradise_client.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DummyPacket() implements CustomPacketPayload {
  public static final StreamCodec<FriendlyByteBuf, DummyPacket> CODEC =
    CustomPacketPayload.codec(DummyPacket::write, DummyPacket::new);

  public DummyPacket(FriendlyByteBuf buf) {
    this();
  }

  public void write(FriendlyByteBuf buf) {
  }

  @Override public Type<? extends CustomPacketPayload> type() {
    return null;
  }
}
