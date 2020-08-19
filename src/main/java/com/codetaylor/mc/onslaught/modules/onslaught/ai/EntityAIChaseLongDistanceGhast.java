package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIChaseLongDistanceGhast
    extends EntityAIBase {

  private final EntityGhast taskOwner;
  private final double speed;

  private EntityLivingBase attackTarget;
  private int executionCheckCooldownTicks;

  public EntityAIChaseLongDistanceGhast(EntityGhast taskOwner, double speed) {

    this.taskOwner = taskOwner;
    this.speed = speed;
    this.setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {

    if (this.executionCheckCooldownTicks-- > 0) {
      return false;
    }

    this.executionCheckCooldownTicks += this.taskOwner.getRNG().nextInt(5) + 2;

    this.attackTarget = this.taskOwner.getAttackTarget();

    if (this.attackTarget == null) {
      return false;
    }

    if (this.taskOwner.getDistanceSq(this.attackTarget) < 64 * 64) {
      return false;
    }

    AxisAlignedBB axisalignedbb = this.taskOwner.getEntityBoundingBox();

    if (!this.taskOwner.world.getCollisionBoxes(this.taskOwner, axisalignedbb).isEmpty()) {
      return false;
    }

    return true;
  }

  @Override
  public boolean shouldContinueExecuting() {

    return false;
  }

  @Override
  public void startExecuting() {

    double x = this.attackTarget.posX;
    double y = this.attackTarget.posY;
    double z = this.attackTarget.posZ;
    this.taskOwner.getMoveHelper().setMoveTo(x, y, z, this.speed);
  }
}
