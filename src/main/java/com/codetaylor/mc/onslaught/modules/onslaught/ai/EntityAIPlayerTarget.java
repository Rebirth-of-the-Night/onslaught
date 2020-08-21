package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.function.Supplier;

/**
 * Responsible for providing a persistent player target.
 */
public class EntityAIPlayerTarget
    extends EntityAIBase {

  private final EntityLiving taskOwner;
  private final Supplier<EntityPlayer> playerSupplier;

  private EntityPlayer targetPlayer;

  public EntityAIPlayerTarget(EntityLiving taskOwner, Supplier<EntityPlayer> playerSupplier) {

    this.taskOwner = taskOwner;

    this.playerSupplier = playerSupplier;
    this.setMutexBits(0);
  }

  @Override
  public boolean shouldExecute() {

    EntityLivingBase attackTarget = this.taskOwner.getAttackTarget();

    // Don't run if we already have a target and the target is alive
    if (attackTarget != null && attackTarget.isEntityAlive()) {
      return false;
    }

    this.targetPlayer = this.playerSupplier.get();

    // Ensure the target player exists
    if (this.targetPlayer == null) {
      return false;
    }

    if (!this.targetPlayer.isEntityAlive()) {
      return false;
    }

    // Ensure that the targeted player is in the same world as the mob
    if (this.taskOwner.world.provider.getDimension() != this.targetPlayer.world.provider.getDimension()) {
      return false;
    }

    return true;
  }

  @Override
  public void startExecuting() {

    this.taskOwner.setAttackTarget(this.targetPlayer);
    super.startExecuting();
  }

  @Override
  public boolean shouldContinueExecuting() {

    return false;
  }
}
