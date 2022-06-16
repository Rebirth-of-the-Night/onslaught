package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData.InvasionData.MobData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData.InvasionData.WaveData;
import java.util.function.Predicate;

/** Responsible for returning true if an active invasion is complete. */
public class InvasionFinishedPredicate implements Predicate<InvasionPlayerData> {

  @Override
  public boolean test(InvasionPlayerData invasionPlayerData) {

    if (invasionPlayerData == null) {
      return true;
    }

    if (invasionPlayerData.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active) {
      return false;
    }

    if (invasionPlayerData.getInvasionData() == null) {
      return true;
    }

    for (WaveData data : invasionPlayerData.getInvasionData().getWaveDataList()) {
      for (MobData mobData : data.getMobDataList()) {
        if (mobData.getKilledCount() < mobData.getTotalCount()) {
          return false;
        }
      }
    }
    return true;
  }
}
