package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import java.util.List;

public class InvasionCompletionPercentageCalculator {

  public float calculate(InvasionPlayerData.InvasionData invasionData) {

    int mobCountMax = 0;
    int mobCountKilled = 0;

    List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.getWaveDataList();

    for (InvasionPlayerData.InvasionData.WaveData waveData : waveDataList) {
      List<InvasionPlayerData.InvasionData.MobData> mobDataList = waveData.getMobDataList();

      for (InvasionPlayerData.InvasionData.MobData mobData : mobDataList) {
        mobCountMax += mobData.getTotalCount();
        mobCountKilled += mobData.getKilledCount();
      }
    }

    return (float) mobCountKilled / (float) mobCountMax;
  }
}
