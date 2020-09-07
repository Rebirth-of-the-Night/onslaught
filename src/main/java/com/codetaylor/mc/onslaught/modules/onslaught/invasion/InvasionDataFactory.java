package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.WeightedPicker;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateRegistry;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Responsible for creating {@link InvasionPlayerData.InvasionData} from the
 * given template id and PRNG.
 */
public class InvasionDataFactory {

  public final Supplier<InvasionTemplateRegistry> invasionTemplateRegistrySupplier;

  public InvasionDataFactory(Supplier<InvasionTemplateRegistry> invasionTemplateRegistrySupplier) {

    this.invasionTemplateRegistrySupplier = invasionTemplateRegistrySupplier;
  }

  @Nullable
  public InvasionPlayerData.InvasionData create(String templateId, Random random, long totalWorldTime) {

    InvasionTemplateRegistry invasionTemplateRegistry = this.invasionTemplateRegistrySupplier.get();
    InvasionTemplate invasionTemplate = invasionTemplateRegistry.get(templateId);

    if (invasionTemplate == null) {
      return null;
    }

    InvasionPlayerData.InvasionData invasionData = new InvasionPlayerData.InvasionData();
    invasionData.id = templateId;
    invasionData.timestamp = totalWorldTime + 12000;

    for (InvasionTemplateWave waveTemplate : invasionTemplate.waves) {
      invasionData.waveDataList.add(this.createWaveData(waveTemplate, random));
    }

    return invasionData;
  }

  private InvasionPlayerData.InvasionData.WaveData createWaveData(InvasionTemplateWave waveTemplate, Random random) {

    InvasionPlayerData.InvasionData.WaveData waveData = new InvasionPlayerData.InvasionData.WaveData();
    waveData.delayTicks = this.evaluateRange(waveTemplate.delayTicks, random);

    InvasionTemplateWave.Group group = this.selectGroup(waveTemplate.groups, random);

    for (InvasionTemplateWave.Mob mob : group.mobs) {
      waveData.mobDataList.add(this.createMobData(mob, random));
    }

    return waveData;
  }

  private InvasionPlayerData.InvasionData.MobData createMobData(InvasionTemplateWave.Mob mob, Random random) {

    InvasionPlayerData.InvasionData.MobData mobData = new InvasionPlayerData.InvasionData.MobData();
    mobData.id = mob.id;
    mobData.count = this.evaluateRange(mob.count, random);
    mobData.remainingSpawnCount = mobData.count;
    mobData.killedCount = 0;
    mobData.spawnData = this.createSpawnData(mob.spawn);

    return mobData;
  }

  private InvasionPlayerData.InvasionData.SpawnData createSpawnData(InvasionTemplateWave.Spawn spawn) {

    InvasionPlayerData.InvasionData.SpawnData spawnData = new InvasionPlayerData.InvasionData.SpawnData();
    spawnData.type = spawn.type;
    spawnData.light = Arrays.copyOf(spawn.light, spawn.light.length);
    spawnData.force = spawn.force;
    spawnData.rangeXZ = Arrays.copyOf(spawn.rangeXZ, spawn.rangeXZ.length);
    spawnData.rangeY = spawn.rangeY;
    spawnData.stepRadius = spawn.stepRadius;
    spawnData.sampleDistance = spawn.sampleDistance;

    return spawnData;
  }

  private InvasionTemplateWave.Group selectGroup(InvasionTemplateWave.Group[] groups, Random random) {

    WeightedPicker<InvasionTemplateWave.Group> weightedPicker = new WeightedPicker<>(random);

    for (InvasionTemplateWave.Group group : groups) {
      weightedPicker.add(group.weight, group);
    }

    return weightedPicker.get();
  }

  private int evaluateRange(int[] range, Random random) {

    if (range.length == 1) {
      return range[0];
    }

    int min = Math.min(range[0], range[1]);
    int max = Math.max(range[0], range[1]);

    return random.nextInt(max - min + 1) + min;
  }

}
