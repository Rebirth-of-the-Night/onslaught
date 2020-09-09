package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Spawner {

  private final Function<String, InvasionTemplate> invasionTemplateFunction;
  private final SpawnerWave spawnerWave;

  public Spawner(
      Function<String, InvasionTemplate> invasionTemplateFunction,
      SpawnerWave spawnerWave
  ) {

    this.invasionTemplateFunction = invasionTemplateFunction;
    this.spawnerWave = spawnerWave;
  }

  public void process(
      InvasionGlobalSavedData invasionGlobalSavedData,
      Supplier<List<EntityPlayerMP>> playerListSupplier
  ) {

    long start = System.currentTimeMillis();
    List<EntityPlayerMP> playerList = playerListSupplier.get();

    for (EntityPlayerMP player : playerList) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

      if (data.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      // If the invasion state is active, the invasion data should never be null.
      if (invasionData == null) {
        continue;
      }

      InvasionTemplate invasionTemplate = this.invasionTemplateFunction.apply(invasionData.getInvasionTemplateId());

      if (invasionTemplate == null) {
        // If this happens, the invasion id has changed since the invasion data was created.
        // Nullify the invasion to flag it as complete.
        // The state change processors will clean this up.
        data.setInvasionData(null);
        continue;
      }

      List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.getWaveDataList();

      for (int waveIndex = 0; waveIndex < waveDataList.size(); waveIndex++) {
        InvasionPlayerData.InvasionData.WaveData waveData = waveDataList.get(waveIndex);
        InvasionTemplateWave templateWave = invasionTemplate.waves[waveIndex];

        if (waveData.getDelayTicks() <= 0) {

          if (this.spawnerWave.attemptSpawnWave(start, player, waveIndex, waveData, templateWave.secondaryMob)) {
            return;
          }
        }
      }
    }
  }
}
