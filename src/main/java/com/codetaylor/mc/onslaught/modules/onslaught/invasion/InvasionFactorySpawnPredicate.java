package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.Util;
import net.minecraft.entity.EntityLiving;

import java.util.function.Predicate;

public class InvasionFactorySpawnPredicate {

  public Predicate<EntityLiving> create(InvasionPlayerData.InvasionData.SpawnData spawnData) {

    switch (spawnData.type) {

      case ground:
        int[] light = Util.evaluateRangeArray(spawnData.light);
        return new InvasionSpawnPredicateGround(light[0], light[1], spawnData.rangeY);

      case air:
        // TODO

      default:
        throw new IllegalArgumentException("Unknown spawn type: " + spawnData.type);
    }
  }
}
