package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Responsible for pathing an entity toward its target over a long distance by
 * splitting the path into smaller sections.
 */
public class EntityAIChaseLongDistance
    extends EntityAIBase {

  private final EntityLiving taskOwner;
  private final double speed;
  private Path path;

  public EntityAIChaseLongDistance(EntityLiving taskOwner, double speed) {

    this.taskOwner = taskOwner;
    this.speed = speed;
    this.setMutexBits(1);
  }

  @Override
  public boolean shouldExecute() {

    EntityLivingBase target = this.taskOwner.getAttackTarget();

    if (target == null) {
      return false;
    }

    double followRange = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();

    if (this.taskOwner.getDistanceSq(target) < followRange * followRange) {
      return false;
    }

    PathNavigate navigator = this.taskOwner.getNavigator();

    if (!navigator.noPath()) {
      return false;
    }

//    if (target instanceof EntityPlayer) {
//      EntityPlayer player = (EntityPlayer) target;
//
//      if (player.isSpectator() || player.isCreative()) {
//        return false;
//      }
//    }

    Vec3d originVector = this.taskOwner.getPositionVector();
    Vec3d targetVector = target.getPositionVector();
    Random random = this.taskOwner.getRNG();
    int searchRangeXZ = 4;
    int searchRangeY = 16;
    int distance = (int) Math.max(0, followRange - searchRangeXZ * 2);

    Vec3d pathVec = this.findTargetBlock(this.taskOwner, navigator, random, originVector, targetVector, distance, searchRangeXZ, searchRangeY);

//    if (pathVec == null) {
//      pathVec = RandomPositionGenerator.findRandomTargetBlockTowards(this.taskOwner, (int) (followRange * 0.75), 64, targetVector);
//    }

    if (pathVec != null) {
      this.path = navigator.getPathToXYZ(pathVec.x, pathVec.y, pathVec.z);
    }

    System.out.println(this.path);

    return (this.path != null);
  }

  @Override
  public boolean shouldContinueExecuting() {

    EntityLivingBase target = this.taskOwner.getAttackTarget();

    if (target == null) {
      return false;
    }

    if (!target.isEntityAlive()) {
      return false;
    }

    if (this.taskOwner.getNavigator().noPath()) {
      return false;
    }

//    if (target instanceof EntityPlayer) {
//      EntityPlayer player = (EntityPlayer) target;
//
//      if (player.isSpectator() || player.isCreative()) {
//        return false;
//      }
//    }

    double followRange = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();

    return (this.taskOwner.getDistanceSq(target) >= followRange * followRange);
  }

  @Override
  public void startExecuting() {

    this.taskOwner.getNavigator().setPath(this.path, this.speed);
  }

  @Override
  public void resetTask() {

    this.taskOwner.getNavigator().clearPath();
  }

  /**
   * Searches 10 blocks at random within searchRangeXZ and searchRangeY distance
   * from a point located between origin and target at the given distance.
   *
   * @see RandomPositionGenerator#generateRandomPos(EntityCreature, int, int, Vec3d, boolean)
   */
  @SuppressWarnings("JavadocReference")
  @Nullable
  private Vec3d findTargetBlock(EntityLiving entity, PathNavigate navigator, Random random, Vec3d origin, Vec3d target, int distance, int searchRangeXZ, int searchRangeY) {

    Vec3d subTarget = target.subtract(origin).normalize().scale(distance).add(origin);

    float largestBlockPathWeight = -99999;
    int storedX = 0;
    int storedY = 0;
    int storedZ = 0;
    boolean validBlockPosFound = false;

    for (int i = 0; i < 10; i++) {

      int x = random.nextInt(2 * searchRangeXZ + 1) - searchRangeXZ;
      int y = random.nextInt(2 * searchRangeY + 1) - searchRangeY;
      int z = random.nextInt(2 * searchRangeXZ + 1) - searchRangeXZ;

      if (x * target.x + z * target.z < 0) {
        continue;
      }

      BlockPos blockPos = new BlockPos(x + subTarget.x, y + subTarget.y, z + subTarget.z);

      if (navigator.canEntityStandOnPos(blockPos)) {

        float blockPathWeight = (entity instanceof EntityCreature) ? ((EntityCreature) entity).getBlockPathWeight(blockPos) : 0;

        if (blockPathWeight > largestBlockPathWeight) {
          largestBlockPathWeight = blockPathWeight;
          storedX = x;
          storedY = y;
          storedZ = z;
          validBlockPosFound = true;
        }
      }
    }

    if (validBlockPosFound) {
      return new Vec3d(storedX + subTarget.x, storedY + subTarget.y, storedZ + subTarget.z);
    }

    return null;
  }
}