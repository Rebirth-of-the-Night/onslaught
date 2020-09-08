package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * Responsible for transitioning a player's invasion state from active to waiting.
 */
public class StateChangeActiveToWaiting {

  public void process(EntityPlayer player) {

    IInvasionPlayerData data = CapabilityInvasion.get(player);
    InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

    if (this.isInvasionFinished(invasionData)) {
      data.setInvasionState(IInvasionPlayerData.EnumInvasionState.Waiting);
      data.setInvasionData(null);

      int min = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[0];
      int max = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[1];
      min = Math.min(max, min);
      max = Math.max(max, min);
      int ticksUntilEligible = RandomHelper.random().nextInt(max - min + 1) + min;

      data.setTicksUntilEligible(ticksUntilEligible);
    }
  }

  private boolean isInvasionFinished(InvasionPlayerData.InvasionData invasionData) {

    if (invasionData == null) {
      return true;
    }

    List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.waveDataList;

    for (InvasionPlayerData.InvasionData.WaveData waveData : waveDataList) {
      List<InvasionPlayerData.InvasionData.MobData> mobDataList = waveData.mobDataList;

      for (InvasionPlayerData.InvasionData.MobData mobData : mobDataList) {

        if (mobData.killedCount != mobData.count) {
          return false;
        }
      }
    }

    return true;
  }
}
