package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;

import java.util.List;
import java.util.function.Predicate;

/**
 * Responsible for returning true if the given invasion data has an active wave.
 */
public class ActiveWavePredicate
    implements Predicate<InvasionPlayerData.InvasionData> {

  @Override
  public boolean test(InvasionPlayerData.InvasionData invasionData) {

    if (invasionData == null) {
      return false;
    }

    List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.getWaveDataList();

    for (InvasionPlayerData.InvasionData.WaveData waveData : waveDataList) {
      List<InvasionPlayerData.InvasionData.MobData> mobDataList = waveData.getMobDataList();

      for (InvasionPlayerData.InvasionData.MobData mobData : mobDataList) {

        if (mobData.getKilledCount() < mobData.getTotalCount()) {
          return true;
        }
      }
    }

    return false;
  }
}
