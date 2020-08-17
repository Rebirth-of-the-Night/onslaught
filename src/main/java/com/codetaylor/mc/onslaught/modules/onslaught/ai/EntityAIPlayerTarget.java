package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

import java.util.function.Supplier;

public class EntityAIPlayerTarget
    extends EntityAITarget {

  private static final int TARGET_DISTANCE = 9999;
  private final Supplier<EntityPlayer> playerSupplier;

  private EntityPlayer targetPlayer;

  public EntityAIPlayerTarget(EntityCreature creature, Supplier<EntityPlayer> playerSupplier) {

    super(creature, false, false);
    this.playerSupplier = playerSupplier;
    this.setMutexBits(0);
  }

  @Override
  public boolean shouldExecute() {

    this.targetPlayer = this.playerSupplier.get();

    // Ensure the target player exists
    if (this.targetPlayer == null) {
      System.out.println("Target player == null");
      return false;
    }

    // Ensure that the targeted player is in the same world as the mob
    if (this.taskOwner.world.provider.getDimension() != this.targetPlayer.world.provider.getDimension()) {
      System.out.println("Target dimension != task owner dimension");
      return false;
    }

    System.out.println("Targeting player");
    return true;
  }

  @Override
  public void startExecuting() {

    this.taskOwner.setAttackTarget(this.targetPlayer);
    super.startExecuting();
  }

  @Override
  public boolean shouldContinueExecuting() {

    // Ensure the target player exists
    if (this.targetPlayer == null) {
      System.out.println("Target player == null");
      return false;
    }

    // Ensure that the targeted player is in the same world as the mob
    if (this.taskOwner.world.provider.getDimension() != this.targetPlayer.world.provider.getDimension()) {
      System.out.println("Target dimension != task owner dimension");
      return false;
    }

    boolean continueExecuting = super.shouldContinueExecuting();

    if (!continueExecuting) {
      System.out.println("Stopping execution");
    }

    return continueExecuting;
  }

  @Override
  protected double getTargetDistance() {

    return TARGET_DISTANCE;
  }
}
