package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

/**
 * Responsible for allowing a mob leap once at its revenge target.
 *
 * @see net.minecraft.entity.ai.EntityAILeapAtTarget
 */
public class EntityAICounterAttack
    extends EntityAIBase {

  private final EntityLiving taskOwner;
  private final float leapMotionY;
  private final float leapMotionXZ;
  private final float chance;
  private final float rangeMinSq;
  private final float rangeMaxSq;

  private EntityLivingBase target;
  private int revengeTimer;

  public EntityAICounterAttack(EntityLiving taskOwner, float leapMotionXZ, float leapMotionY, float chance, float rangeMin, float rangeMax) {

    this.taskOwner = taskOwner;
    this.leapMotionY = leapMotionY;
    this.leapMotionXZ = leapMotionXZ;
    this.chance = chance;
    this.rangeMinSq = rangeMin * rangeMin;
    this.rangeMaxSq = rangeMax * rangeMax;
    this.setMutexBits(1 | 4);
  }

  @Override
  public boolean shouldExecute() {

    this.target = this.taskOwner.getRevengeTarget();

    if (this.target == null) {
      return false;
    }

    double distanceSq = this.taskOwner.getDistanceSq(this.target);

    if (distanceSq < this.rangeMinSq || distanceSq > this.rangeMaxSq) {
      return false;
    }

    if (this.revengeTimer == this.taskOwner.getRevengeTimer()) {
      return false;
    }

    return (this.taskOwner.onGround && this.taskOwner.getRNG().nextFloat() <= this.chance);
  }

  @Override
  public boolean shouldContinueExecuting() {

    return !this.taskOwner.onGround;
  }

  @Override
  public void startExecuting() {

    this.revengeTimer = this.taskOwner.getRevengeTimer();

    double dx = this.target.posX - this.taskOwner.posX;
    double dz = this.target.posZ - this.taskOwner.posZ;
    float distance = MathHelper.sqrt(dx * dx + dz * dz);

    if ((double) distance >= 0.0001) {
      this.taskOwner.motionX += dx / (double) distance * this.leapMotionXZ + this.taskOwner.motionX * 0.2;
      this.taskOwner.motionZ += dz / (double) distance * this.leapMotionXZ + this.taskOwner.motionZ * 0.2;
    }

    this.taskOwner.motionY = this.leapMotionY;
  }
}
