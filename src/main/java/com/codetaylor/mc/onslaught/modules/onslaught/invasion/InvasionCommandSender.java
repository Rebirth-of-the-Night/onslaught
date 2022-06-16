package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import java.util.logging.Level;
import net.minecraft.command.CommandSenderWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Responsible for sending the given commands to the given player. */
public class InvasionCommandSender {

  private static final Logger LOGGER = LogManager.getLogger(InvasionCommandSender.class);

  public void send(MinecraftServer minecraftServer, String[] commands, EntityPlayerMP player) {

    CommandSenderWrapper sender =
        CommandSenderWrapper.create(player)
            .computePositionVector()
            .withPermissionLevel(2)
            .withSendCommandFeedback(false);

    for (String commandString : commands) {

      try {
        int result = minecraftServer.commandManager.executeCommand(sender, commandString);

        if (result > 0) {
          String message =
              String.format(
                  "Executed invasion command [%s] for player %s", commandString, player.getName());
          ModOnslaught.LOG.info(message);
          LOGGER.info(message);

        } else {
          String message =
              String.format(
                  "Unable to execute invasion command [%s] for player %s",
                  commandString, player.getName());
          ModOnslaught.LOG.log(Level.SEVERE, message);
          LOGGER.error(message);
        }

      } catch (Exception e) {
        String message =
            String.format(
                "Unable to execute invasion command [%s] for player %s",
                commandString, player.getName());
        ModOnslaught.LOG.log(Level.SEVERE, message, e);
        LOGGER.error(message, e);
      }
    }
  }
}
