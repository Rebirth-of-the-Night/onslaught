package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
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
  private final boolean dimHopping;

  /** 1 in X chance for this task to run each tick. 20 averages once a second. */
  private static int runChanceOutcomes = 20;

  public static int getRunChanceOutcomes() {
    return runChanceOutcomes;
  }

  public static void setRunChanceOutcomes(int runChanceOutcomes) {
    EntityAIOffscreenTeleport.runChanceOutcomes = runChanceOutcomes;
  }

  public EntityAIOffscreenTeleport(
      EntityLiving taskOwner, int rangeThreshold, float teleportFactor, boolean ableToHopDim) {
    this.taskOwner = taskOwner;
    this.teleportThresholdSq = rangeThreshold * rangeThreshold;
    this.teleportFactor = teleportFactor;
    this.dimHopping = ableToHopDim;
  }

  /** Returns whether the EntityAIBase should begin execution. */
  public boolean shouldExecute() {
    if (0 != taskOwner.getRNG().nextInt(runChanceOutcomes)) {
      return false;
    }

    EntityLivingBase target = taskOwner.getAttackTarget();

    if (target == null) {
      return false;
    }

    if (this.dimHopping && target.dimension != taskOwner.dimension) {
      return true;
    }

    return taskOwner.getDistanceSq(target) >= teleportThresholdSq;
  }

  /**
   * translate from a to b, by a factor of f
   *
   * <p>Used to determine teleport distances for moving a mob towards it's target. A factor of .5
   * moves it halfway. Values greater than 1.0 will result in going past the target.
   *
   * @param a source distance
   * @param b target distance
   * @param f factor
   * @return resulting location of 'a' is now 'f' times closer to target of 'b'
   */
  public static double towards(double a, double b, float f) {
    return ((b - a) * f) + a;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return false;
  }

  /**
   * Execute a one shot task or start executing a continuous task
   *
   * <p>We will attempt once to teleport the mob towards the target. The mob gets 1 tick of
   * invisibility (to make most mobs less jarring when they relocate and plays its sound at
   * destination. Armor and tools are still visible during teleport.
   *
   * <p>This is counted as an Ender Teleport event on success.
   */
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
    boolean hasTeleported = taskOwner.attemptTeleport(destX, destY, destZ);
    if (hasTeleported) {
      taskOwner.addPotionEffect(invisibilityEffect());
      taskOwner.playLivingSound();
    }
  }

  /**
   * A few ticks of invisibility to make the mob unseen as it is teleported to the other side of the
   * player, when the factor > 1.
   *
   * @return Invisibility Effect
   */
  protected PotionEffect invisibilityEffect() {
    return new PotionEffect(MobEffects.INVISIBILITY, 10);
  }
}
