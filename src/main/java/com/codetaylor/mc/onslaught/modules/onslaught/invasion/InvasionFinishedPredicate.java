package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import java.util.List;
import java.util.function.Predicate;

/**
 * Responsible for returning true if an active invasion is complete.
 */
public class InvasionFinishedPredicate
    implements Predicate<InvasionPlayerData> {

  @Override
  public boolean test(InvasionPlayerData invasionPlayerData) {

    if (invasionPlayerData == null) {
      return true;
    }

    if (invasionPlayerData.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active) {
      return false;
    }

    InvasionPlayerData.InvasionData invasionData = invasionPlayerData.getInvasionData();

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
