package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class InvasionSpawner {

  private final Function<String, InvasionTemplate> invasionTemplateFunction;
  private final InvasionSpawnerWave invasionSpawnerWave;

  public InvasionSpawner(
      Function<String, InvasionTemplate> invasionTemplateFunction,
      InvasionSpawnerWave invasionSpawnerWave
  ) {

    this.invasionTemplateFunction = invasionTemplateFunction;
    this.invasionSpawnerWave = invasionSpawnerWave;
  }

  public void process(
      Supplier<List<EntityPlayerMP>> playerListSupplier
  ) {

    long start = System.currentTimeMillis();
    List<EntityPlayerMP> playerList = playerListSupplier.get();

    for (EntityPlayerMP player : playerList) {
      IInvasionPlayerData data = CapabilityInvasion.get(player);

      if (data.getInvasionState() != IInvasionPlayerData.EnumInvasionState.Active) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      // If the invasion state is active, the invasion data should never be null.
      if (invasionData == null) {
        continue;
      }

      InvasionTemplate invasionTemplate = this.invasionTemplateFunction.apply(invasionData.id);

      if (invasionTemplate == null) {
        // If this happens, the invasion id has changed since the invasion data was created.
        // Nullify the invasion to flag it as complete.
        // The state change processors will clean this up.
        data.setInvasionData(null);
        continue;
      }

      List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.waveDataList;

      for (int waveIndex = 0; waveIndex < waveDataList.size(); waveIndex++) {
        InvasionPlayerData.InvasionData.WaveData waveData = waveDataList.get(waveIndex);
        InvasionTemplateWave templateWave = invasionTemplate.waves[waveIndex];

        if (waveData.delayTicks <= 0) {

          if (this.invasionSpawnerWave.attemptSpawnWave(start, player, waveIndex, waveData, templateWave.secondaryMob)) {
            return;
          }
        }
      }
    }
  }
}
