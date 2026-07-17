package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;
import net.paradise_client.inject.accessor.ClientPlayNetworkHandlerAccessor;

public class SayCommand extends Command {
  public SayCommand() {
    super("say", "Sends a chat message to the server", CommandManager.CommandCategory.UTILITY);
  }

  @Override public void build(LiteralArgumentBuilder<SharedSuggestionProvider> root) {
    root.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
      String message = context.getArgument("message", String.class);
      ((ClientPlayNetworkHandlerAccessor) Minecraft.getInstance()
        .getConnection()).paradiseClient$sendChatMessage(message);
      return SINGLE_SUCCESS;
    }));
  }
}
