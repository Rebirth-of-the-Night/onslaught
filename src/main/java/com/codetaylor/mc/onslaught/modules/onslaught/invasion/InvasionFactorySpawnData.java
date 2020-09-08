package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;

import java.util.Arrays;

public class InvasionFactorySpawnData {

  public InvasionPlayerData.InvasionData.SpawnData create(InvasionTemplateWave.Spawn spawn) {

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
