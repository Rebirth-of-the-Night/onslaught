package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;

import java.util.List;

/**
 * Responsible for transitioning a player's invasion state from active to waiting.
 */
public class StateChangeActiveToWaiting
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    for (EntityPlayerMP player : playerList.getPlayers()) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());
      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      if (this.isInvasionFinished(invasionData)) {
        data.setInvasionState(InvasionPlayerData.EnumInvasionState.Waiting);
        data.setInvasionData(null);

        int min = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[0];
        int max = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[1];
        min = Math.min(max, min);
        max = Math.max(max, min);
        int ticksUntilEligible = RandomHelper.random().nextInt(max - min + 1) + min;

        data.setTicksUntilEligible(ticksUntilEligible);
        invasionGlobalSavedData.markDirty();

        if (ModuleOnslaughtConfig.DEBUG.INVASION_STATE) {
          String message = String.format("Set invasion state to %s for player %s", "Waiting", player.getName());
          ModOnslaught.LOG.fine(message);
          System.out.println(message);
        }
      }
    }
  }

  private boolean isInvasionFinished(InvasionPlayerData.InvasionData invasionData) {

    if (invasionData == null) {
      return true;
    }

    List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.getWaveDataList();

    for (InvasionPlayerData.InvasionData.WaveData waveData : waveDataList) {
      List<InvasionPlayerData.InvasionData.MobData> mobDataList = waveData.getMobDataList();

      for (InvasionPlayerData.InvasionData.MobData mobData : mobDataList) {

        if (mobData.getKilledCount() < mobData.getTotalCount()) {
          return false;
        }
      }
    }

    return true;
  }
}
