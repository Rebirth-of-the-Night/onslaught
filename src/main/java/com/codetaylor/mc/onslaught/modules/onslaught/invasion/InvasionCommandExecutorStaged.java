package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateCommandStaged;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/** Responsible for executing staged invasion commands. */
public class InvasionCommandExecutorStaged {

  private final Function<String, InvasionTemplate> idToInvasionTemplateFunction;
  private final InvasionCompletionPercentageCalculator invasionCompletionPercentageCalculator;
  private final InvasionCommandSender invasionCommandSender;

  public InvasionCommandExecutorStaged(
      Function<String, InvasionTemplate> idToInvasionTemplateFunction,
      InvasionCompletionPercentageCalculator invasionCompletionPercentageCalculator,
      InvasionCommandSender invasionCommandSender) {

    this.idToInvasionTemplateFunction = idToInvasionTemplateFunction;
    this.invasionCompletionPercentageCalculator = invasionCompletionPercentageCalculator;
    this.invasionCommandSender = invasionCommandSender;
  }

  public void execute(EntityPlayerMP player, InvasionPlayerData.InvasionData invasionData) {

    String invasionTemplateId = invasionData.getInvasionTemplateId();
    InvasionTemplate invasionTemplate = this.idToInvasionTemplateFunction.apply(invasionTemplateId);

    if (invasionTemplate == null) {
      return;
    }

    InvasionTemplateCommandStaged[] stagedCommands = invasionTemplate.commands.staged;

    if (stagedCommands.length == 0) {
      return;
    }

    float percentComplete = this.invasionCompletionPercentageCalculator.calculate(invasionData);
    MinecraftServer minecraftServer = player.world.getMinecraftServer();

    if (ModuleOnslaughtConfig.DEBUG.INVASION_COMMANDS) {
      String message = String.format(
          "Invasion with id %s is %f percent complete, querying %d staged commands",
          invasionData.getInvasionTemplateId(), percentComplete, stagedCommands.length);
      ModOnslaught.LOG.fine(message);
      System.out.println(message);
    }

    for (int i = 0; i < stagedCommands.length; i++) {

      if (invasionData.isStagedCommandFlagSet(i)) {
        continue;
      }

      InvasionTemplateCommandStaged stagedCommand = stagedCommands[i];

      if (stagedCommand.commands.length == 0) {
        continue;
      }

      if (stagedCommand.complete <= percentComplete) {
        invasionData.setStagedCommandFlag(i);
        InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(player.world);
        invasionGlobalSavedData.markDirty();

        this.invasionCommandSender.send(minecraftServer, stagedCommand.commands, player);
      }
    }
  }
}
