package com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.predicate;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.Util;
import java.util.function.Predicate;
import net.minecraft.entity.EntityLiving;

/**
 * Responsible for creating a spawn predicate from the given {@link
 * InvasionPlayerData.InvasionData.SpawnData}.
 */
public class SpawnPredicateFactory {

  public Predicate<EntityLiving> create(InvasionPlayerData.InvasionData.SpawnData spawnData) {

    switch (spawnData.type) {
      case ground:
        {
          int[] light = Util.evaluateRangeArray(spawnData.light);
          return new SpawnPredicateGround(light[0], light[1], spawnData.rangeY);
        }

      case air:
        {
          int[] light = Util.evaluateRangeArray(spawnData.light);
          return new SpawnPredicateAir(light[0], light[1], spawnData.rangeY);
        }

      case beneath:
      {
        int[] light = Util.evaluateRangeArray(spawnData.light);
        return new SpawnPredicateBeneath(light[0], light[1], spawnData.rangeY);
      }

      default:
        throw new IllegalArgumentException("Unknown spawn type: " + spawnData.type);
    }
  }
}
