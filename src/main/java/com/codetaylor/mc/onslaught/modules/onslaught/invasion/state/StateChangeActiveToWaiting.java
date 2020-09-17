package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionStopExecutor;
import net.minecraft.server.management.PlayerList;

/**
 * Responsible for transitioning a player's invasion state from active to waiting.
 */
public class StateChangeActiveToWaiting
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final InvasionStopExecutor invasionStopExecutor;

  public StateChangeActiveToWaiting(
      InvasionStopExecutor invasionStopExecutor
  ) {

    this.invasionStopExecutor = invasionStopExecutor;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    this.invasionStopExecutor.stopAllWithCheck(playerList.getPlayers(), invasionGlobalSavedData);
  }
}
