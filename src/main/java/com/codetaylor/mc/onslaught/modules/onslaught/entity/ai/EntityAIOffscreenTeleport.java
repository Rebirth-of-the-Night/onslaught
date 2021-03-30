package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.client.event.sound.SoundEvent;
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

  /** the range the mob will teleport to */
  private final int closeToRange;

  /** may the mob breach not just space but dimension to chase its prey */
  private final boolean ableToDimHop;

  /** internal counter to stagger the checking to teleport. */
  private int counter;

  /** total ticks */
  private static final int TICK_PERIOD = 5;

  private boolean telported = false;

  public EntityAIOffscreenTeleport(
      EntityLiving taskOwner, int rangeThreshold, int closeToRange, boolean ableToDimHop) {
    this.taskOwner = taskOwner;
    this.teleportThresholdSq = rangeThreshold * rangeThreshold;
    this.closeToRange = closeToRange;
    this.ableToDimHop = ableToDimHop;
    this.counter = taskOwner.getRNG().nextInt(TICK_PERIOD);
  }

  /** Returns whether the EntityAIBase should begin execution. */
  public boolean shouldExecute() {
    if (counter++ < TICK_PERIOD) {
      return false;
    }
    counter = 0;

    EntityLivingBase target = taskOwner.getAttackTarget();

    if (target == null) {
      return false;
    }

    if (this.ableToDimHop && target.dimension != taskOwner.dimension) {
      return true;
    }

    return taskOwner.getDistanceSq(target) >= teleportThresholdSq;
  }

  /** Execute a one shot task or start executing a continuous task */
  public void startExecuting() {
    EntityLivingBase target = taskOwner.getAttackTarget();
    if (target == null) {
      System.out.println("no target");
      return;
    }

    int offsetX = taskOwner.getRNG().nextBoolean() ? closeToRange : -closeToRange;
    int offsetZ = taskOwner.getRNG().nextBoolean() ? closeToRange : -closeToRange;

    double teleX = target.posX + offsetX;
    double teleY = target.posY + 4;
    double teleZ = target.posZ + offsetZ;

    EnderTeleportEvent event = new EnderTeleportEvent(taskOwner, teleX, teleY, teleZ, 0);
    boolean wasCanceled = EVENT_BUS.post(event);
    System.out.println("was canceled:" + wasCanceled);
    if (wasCanceled) {
      return;
    }

    taskOwner.setWorld(target.getEntityWorld());
    telported = taskOwner.attemptTeleport(teleX, teleY, teleZ);
    if (telported) {
      taskOwner.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, .4F);
      ((EntityLiving)taskOwner).playLivingSound();
    }
    System.out.println("was teleported:" + telported);
  }

  /** Reset the task's internal state. Called when this task is interrupted by another one */
  public void resetTask() {}

  /** Returns whether an in-progress EntityAIBase should continue executing */
  public boolean shouldContinueExecuting() {
    return !telported;
  }

  /** Keep ticking a continuous task that has already been started */
  public void updateTask() {
    startExecuting();
  }
}
