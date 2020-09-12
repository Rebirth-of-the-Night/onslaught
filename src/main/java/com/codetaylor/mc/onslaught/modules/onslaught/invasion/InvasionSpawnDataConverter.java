package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;

import java.util.Arrays;

/**
 * Responsible for converting {@link InvasionTemplateWave.Spawn} template data
 * into {@link InvasionPlayerData.InvasionData.SpawnData} player data.
 */
public class InvasionSpawnDataConverter {

  public InvasionPlayerData.InvasionData.SpawnData convert(InvasionTemplateWave.Spawn spawn) {

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
}
