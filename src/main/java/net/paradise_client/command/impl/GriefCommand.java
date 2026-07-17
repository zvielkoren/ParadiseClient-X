package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.SharedSuggestionProvider;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

import java.util.Objects;

public class GriefCommand extends Command {

  /**
   * Constructs a new instance of {@link GriefCommand}.
   */
  public GriefCommand() {
    super("grief", "Multiple grief commands", CommandManager.CommandCategory.UTILITY);
  }

  /**
   * Builds the command structure using Brigadier's {@link LiteralArgumentBuilder}.
   */
  @Override public void build(LiteralArgumentBuilder<SharedSuggestionProvider> root) {
    root.then(literal("tpall").executes((context) -> {
      ClientPacketListener handler = Minecraft.getInstance().getConnection();
      handler.sendCommand("tpall");
      handler.sendCommand("etpall");
      handler.sendCommand("minecraft:tp @a @p");
      handler.sendCommand("tp @a @p");
      return SINGLE_SUCCESS;
    })).then(literal("fill").then(literal("air").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getConnection())
        .sendCommand("minecraft:fill ~12 ~12 ~12 ~-12 ~-12 ~-12 air");
      return SINGLE_SUCCESS;
    })).then(literal("lava").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getConnection())
        .sendCommand("minecraft:fill ~12 ~12 ~12 ~-12 ~-12 ~-12 lava");
      return SINGLE_SUCCESS;
    })).executes(this::incompleteCommand)).then(literal("sphere").then(literal("air").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getConnection()).sendCommand("/sphere air 10");
      return SINGLE_SUCCESS;
    })).then(literal("lava").executes((context) -> {
      Objects.requireNonNull(getMinecraftClient().getConnection()).sendCommand("/sphere lava 10");
      return SINGLE_SUCCESS;
    })).executes(this::incompleteCommand)).executes(this::incompleteCommand);
  }
}
