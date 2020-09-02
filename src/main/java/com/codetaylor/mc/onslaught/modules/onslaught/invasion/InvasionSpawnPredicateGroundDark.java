package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import net.minecraft.entity.EntityLiving;

public class InvasionSpawnPredicateGroundDark
    extends InvasionSpawnPredicateGroundBase {

  private final int requiredLightLevelMax;

  public InvasionSpawnPredicateGroundDark(int requiredLightLevelMax) {

    super();
    this.requiredLightLevelMax = requiredLightLevelMax;
  }

  @Override
  public boolean test(EntityLiving entity) {

    this.blockPos.setPos(entity);

    // Ensure the light level isn't above the max
    if (entity.world.getLight(this.blockPos) > this.requiredLightLevelMax) {
      return false;
    }

    return super.test(entity);
  }
}
