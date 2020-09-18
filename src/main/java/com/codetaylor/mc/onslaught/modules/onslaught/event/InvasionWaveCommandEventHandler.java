package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCommandExecutor;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Responsible for executing commands in response to an invasion begin or end.
 */
public class InvasionWaveCommandEventHandler {

  private final InvasionCommandExecutor commandExecutorStart;
  private final InvasionCommandExecutor commandExecutorEnd;

  public InvasionWaveCommandEventHandler(
      InvasionCommandExecutor commandExecutorStart,
      InvasionCommandExecutor commandExecutorEnd
  ) {

    this.commandExecutorStart = commandExecutorStart;
    this.commandExecutorEnd = commandExecutorEnd;
  }

  @SubscribeEvent
  public void on(InvasionStateChangedEvent event) {

    InvasionPlayerData.EnumInvasionState currentState = event.getCurrentState();

    if (currentState == InvasionPlayerData.EnumInvasionState.Active) {
      this.commandExecutorStart.execute(event.getPlayer());

    } else if (currentState == InvasionPlayerData.EnumInvasionState.Waiting) {
      this.commandExecutorEnd.execute(event.getPlayer());
    }

  }
}