package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import net.minecraft.command.CommandSenderWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.logging.Level;

/**
 * Responsible for executing an invasion command.
 */
public class InvasionCommandExecutor {

  private static final Logger LOGGER = LogManager.getLogger(InvasionCommandExecutor.class);

  private final Function<String, InvasionTemplate> idToInvasionTemplateFunction;
  private final Function<InvasionTemplate, String[]> commandFunction;

  public InvasionCommandExecutor(
      Function<String, InvasionTemplate> idToInvasionTemplateFunction,
      Function<InvasionTemplate, String[]> commandFunction
  ) {

    this.idToInvasionTemplateFunction = idToInvasionTemplateFunction;
    this.commandFunction = commandFunction;
  }

  public void execute(EntityPlayerMP player) {

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(player.world);
    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());
    InvasionPlayerData.InvasionData invasionData = playerData.getInvasionData();

    if (invasionData == null) {
      return;
    }

    MinecraftServer minecraftServer = player.world.getMinecraftServer();

    if (minecraftServer == null) {
      return;
    }

    String invasionTemplateId = invasionData.getInvasionTemplateId();
    InvasionTemplate invasionTemplate = this.idToInvasionTemplateFunction.apply(invasionTemplateId);
    String[] commandStrings = this.commandFunction.apply(invasionTemplate);

    if (commandStrings.length == 0) {
      return;
    }

    CommandSenderWrapper sender = CommandSenderWrapper
        .create(player)
        .computePositionVector()
        .withPermissionLevel(2)
        .withSendCommandFeedback(false);

    for (String commandString : commandStrings) {

      try {
        int result = minecraftServer.commandManager.executeCommand(sender, commandString);

        if (result > 0) {
          String message = String.format("Executed invasion command [%s] for player %s", commandString, player.getName());
          ModOnslaught.LOG.info(message);
          LOGGER.info(message);

        } else {
          String message = String.format("Unable to execute invasion command [%s] for player %s", commandString, player.getName());
          ModOnslaught.LOG.log(Level.SEVERE, message);
          LOGGER.error(message);
        }

      } catch (Exception e) {
        String message = String.format("Unable to execute invasion command [%s] for player %s", commandString, player.getName());
        ModOnslaught.LOG.log(Level.SEVERE, message, e);
        LOGGER.error(message, e);
      }
    }
  }
}