package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIExplodeWhenStuck
    extends EntityAIBase {

  private final EntityLiving taskOwner;
  private final boolean sightRequired;
  private final boolean rangeRequired;
  private final float rangeMinSq;
  private final float rangeMaxSq;
  private final int explosionDelayTicks;
  private final float explosionStrength;
  private final boolean explosionCausesFire;
  private final boolean explosionDamaging;

  private int explosionDelayCounterTicks;

  public EntityAIExplodeWhenStuck(
      EntityLiving taskOwner,
      boolean sightRequired,
      boolean rangeRequired,
      float rangeMin,
      float rangeMax,
      int explosionDelayTicks,
      float explosionStrength,
      boolean explosionCausesFire,
      boolean explosionDamaging
  ) {

    this.taskOwner = taskOwner;
    this.sightRequired = sightRequired;
    this.rangeRequired = rangeRequired;
    this.rangeMinSq = rangeMin * rangeMin;
    this.rangeMaxSq = rangeMax * rangeMax;
    this.explosionDelayTicks = explosionDelayTicks;
    this.explosionStrength = explosionStrength;
    this.explosionCausesFire = explosionCausesFire;
    this.explosionDamaging = explosionDamaging;

    this.setMutexBits(0);
  }

  @Override
  public boolean shouldExecute() {

    return this.isStuck();
  }

  @Override
  public boolean shouldContinueExecuting() {

    return this.isStuck();
  }

  @Override
  public void resetTask() {

    this.explosionDelayCounterTicks = 0;
  }

  @Override
  public void updateTask() {

    this.explosionDelayCounterTicks += 1;

    if (this.explosionDelayCounterTicks >= this.explosionDelayTicks) {
      this.taskOwner.world.newExplosion(
          this.taskOwner,
          this.taskOwner.posX,
          this.taskOwner.posY,
          this.taskOwner.posZ,
          this.explosionStrength,
          this.explosionCausesFire,
          this.explosionDamaging
      );
      this.taskOwner.onKillCommand();
    }
  }

  private boolean isStuck() {

    EntityLivingBase attackTarget = this.taskOwner.getAttackTarget();

    if (attackTarget == null) {
      return false;
    }

    if (!attackTarget.isEntityAlive()) {
      return false;
    }

    if (!this.taskOwner.getNavigator().noPath()) {
      return false;
    }

    if (this.sightRequired && !this.taskOwner.canEntityBeSeen(attackTarget)) {
      return false;
    }

    if (this.rangeRequired) {
      double distanceSq = this.taskOwner.getDistanceSq(attackTarget);

      if (distanceSq < this.rangeMinSq || distanceSq > this.rangeMaxSq) {
        return false;
      }
    }

    return true;
  }
}
