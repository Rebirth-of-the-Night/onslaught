package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.function.Function;

/**
 * Responsible for executing an invasion command.
 */
public class InvasionCommandExecutor {

  private final Function<String, InvasionTemplate> idToInvasionTemplateFunction;
  private final Function<InvasionTemplate, String[]> commandFunction;
  private final InvasionCommandSender invasionCommandSender;

  public InvasionCommandExecutor(
      Function<String, InvasionTemplate> idToInvasionTemplateFunction,
      Function<InvasionTemplate, String[]> commandFunction,
      InvasionCommandSender invasionCommandSender
  ) {

    this.idToInvasionTemplateFunction = idToInvasionTemplateFunction;
    this.commandFunction = commandFunction;
    this.invasionCommandSender = invasionCommandSender;
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

    this.invasionCommandSender.send(minecraftServer, commandStrings, player);
  }
}