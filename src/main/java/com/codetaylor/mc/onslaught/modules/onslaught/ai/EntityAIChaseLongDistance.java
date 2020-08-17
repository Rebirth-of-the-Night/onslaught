package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.Vec3d;

public class EntityAIChaseLongDistance
    extends EntityAIBase {

  public static final double DEFAULT_SPEED = 1.0;

  private final EntityCreature attacker;
  private final double speed;
  private Path path;

  public EntityAIChaseLongDistance(EntityCreature attacker, double speed) {

    this.attacker = attacker;
    this.speed = speed;
    this.setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {

    EntityLivingBase target = this.attacker.getAttackTarget();

    // If no target, don't execute
    if (target == null) {
      return false;
    }

    double followRange = this.attacker.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();

    // If within normal range, don't execute
    if (this.attacker.getDistanceSq(target) < followRange * followRange) {
      return false;
    }

    if (!this.attacker.getNavigator().noPath()) {
      return false;
    }

    if (target instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) target;

      if (player.isSpectator() || player.isCreative()) {
        return false;
      }
    }

    Vec3d pathVec = RandomPositionGenerator.findRandomTargetBlockTowards(this.attacker, (int) followRange / 2, 64, target.getPositionVector());

    if (pathVec != null) {
      PathNavigate navigator = this.attacker.getNavigator();
      this.path = navigator.getPathToXYZ(pathVec.x, pathVec.y, pathVec.z);
    }

    System.out.println(this.path);

    return (this.path != null);
  }

  @Override
  public boolean shouldContinueExecuting() {

    EntityLivingBase target = this.attacker.getAttackTarget();

    // If no target, don't execute
    if (target == null) {
      return false;
    }

    if (!target.isEntityAlive()) {
      return false;
    }

    if (this.attacker.getNavigator().noPath()) {
      return false;
    }

    if (target instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) target;

      if (player.isSpectator() || player.isCreative()) {
        return false;
      }
    }

    double followRange = this.attacker.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();

    // If within normal range, don't execute
    return !(this.attacker.getDistanceSq(target) < followRange * followRange);
  }

  @Override
  public void startExecuting() {

    this.attacker.getNavigator().setPath(this.path, this.speed);
  }

  @Override
  public void resetTask() {

    this.attacker.getNavigator().clearPath();
  }
}
