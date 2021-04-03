package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

/**
 * Grants the mob the ability to <a
 * href=https://tvtropes.org/pmwiki/pmwiki.php/Main/OffscreenTeleportation>Offscreen Teleport</a>
 * like a horror movie monster.
 */
public class EntityAIOffscreenTeleport extends EntityAIBase {

  /** The mob */
  private final EntityLiving taskOwner;
  /** the range (sq) that the target must breach to try to teleport */
  private final int teleportThresholdSq;

  /** the factor to apply to the vector the mob will roughly teleport to. */
  private final float teleportFactor;

  /** may the mob breach not just space but dimension to chase its prey */
  private final boolean ableToDimHop;

  /** 1:X chance for this task to run */
  private static int chanceOutOfTicks = 20;

  public static int getChanceOutOfTicks() {
    return chanceOutOfTicks;
  }

  public static void setChanceOutOfTicks(int chanceOutOfTicks) {
    EntityAIOffscreenTeleport.chanceOutOfTicks = chanceOutOfTicks;
  }

  private boolean teleported = false;

  public EntityAIOffscreenTeleport(
      EntityLiving taskOwner, int rangeThreshold, float teleportFactor, boolean ableToDimHop) {
    this.taskOwner = taskOwner;
    this.teleportThresholdSq = rangeThreshold * rangeThreshold;
    this.teleportFactor = teleportFactor;
    this.ableToDimHop = ableToDimHop;
  }

  /** Returns whether the EntityAIBase should begin execution. */
  public boolean shouldExecute() {
    if (0 != taskOwner.getRNG().nextInt(chanceOutOfTicks)) {
      return false;
    }

    EntityLivingBase target = taskOwner.getAttackTarget();

    if (target == null) {
      return false;
    }

    if (this.ableToDimHop && target.dimension != taskOwner.dimension) {
      return true;
    }

    return taskOwner.getDistanceSq(target) >= teleportThresholdSq;
  }

  public static double towards(double a, double b, float f){
    return ((b - a) * f) + a;
  }

  /** Execute a one shot task or start executing a continuous task */
  public void startExecuting() {
    EntityLivingBase target = taskOwner.getAttackTarget();
    if (target == null) {
      return;
    }

    double destX = towards(taskOwner.posX, target.posX, teleportFactor);
    double destY = towards(taskOwner.posY, target.posY, teleportFactor);
    double destZ = towards(taskOwner.posZ, target.posZ, teleportFactor);

    EnderTeleportEvent event = new EnderTeleportEvent(taskOwner, destX, destY, destZ, 0);
    boolean wasCanceled = EVENT_BUS.post(event);
    if (wasCanceled) {
      return;
    }

    taskOwner.setWorld(target.getEntityWorld());
    teleported = taskOwner.attemptTeleport(destX, destY, destZ);
    if (teleported) {
      taskOwner.playLivingSound();
    }
  }

  /** Reset the task's internal state. Called when this task is interrupted by another one */
  public void resetTask() {}

  /** Returns whether an in-progress EntityAIBase should continue executing */
  public boolean shouldContinueExecuting() {
    return !teleported;
  }

  /** Keep ticking a continuous task that has already been started */
  public void updateTask() {
    startExecuting();
  }
}
