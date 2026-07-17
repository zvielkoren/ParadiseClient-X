package net.paradise_client.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;
import net.paradise_client.Helper;

import java.util.concurrent.CompletableFuture;

public abstract class Command {
  protected static final int SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

  private final String name;
  private final String description;
  private final boolean async;
  private final CommandManager.CommandCategory category;

  public Command(String name, String description, CommandManager.CommandCategory category) {
    this(name, description, category, false);
  }

  public Command(String name, String description, CommandManager.CommandCategory category, boolean async) {
    this.name = name;
    this.description = description;
    this.category = category;
    this.async = async;
  }

  protected static LiteralArgumentBuilder<SharedSuggestionProvider> literal(final String name) {
    return LiteralArgumentBuilder.literal(name);
  }

  protected static <T> RequiredArgumentBuilder<SharedSuggestionProvider, T> argument(final String name,
                                                                          final ArgumentType<T> type) {
    return RequiredArgumentBuilder.argument(name, type);
  }

  public abstract void build(LiteralArgumentBuilder<SharedSuggestionProvider> root);

  public CompletableFuture<Suggestions> suggestOnlinePlayers(CommandContext<?> ctx, SuggestionsBuilder builder) {
    String partialName;
    try {
      partialName = ctx.getArgument("user", String.class).toLowerCase();
    } catch (IllegalArgumentException ignored) {
      partialName = "";
    }

    if (partialName.isEmpty()) {
      getMinecraftClient().getConnection()
              .getOnlinePlayers()
              .forEach(playerListEntry -> builder.suggest(playerListEntry.getProfile().getName()));
      return builder.buildFuture();
    }

    String finalPartialName = partialName;
    getMinecraftClient().getConnection()
            .getOnlinePlayers()
            .stream()
            .map(PlayerInfo::getProfile)
            .filter(player -> player.getName().toLowerCase().startsWith(finalPartialName.toLowerCase()))
            .forEach(profile -> builder.suggest(profile.getName()));

    return builder.buildFuture();
  }

  public Minecraft getMinecraftClient() {
    return Minecraft.getInstance();
  }

  public int incompleteCommand(CommandContext<?> context) {
    Helper.printChatMessage("Incomplete command!");
    return 1;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isAsync() {
    return async;
  }

  public CommandManager.CommandCategory getCategory() {
    return category;
  }
}
