package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

import java.util.UUID;

public class EntityAILunge
    extends EntityAIBase {

  private static final UUID LUNGE_SPEED_BOOST_ID = UUID.fromString("f36a07f7-1f55-474d-a47f-ded262c548b4");

  private final EntityLiving taskOwner;
  private final double speedModifier;
  private final int rangeSq;

  private IAttributeInstance attributeInstance;

  public EntityAILunge(EntityLiving taskOwner, int range, double speedModifier) {

    this.taskOwner = taskOwner;
    this.speedModifier = speedModifier;
    this.rangeSq = range * range;
    this.setMutexBits(0);
  }

  @Override
  public boolean shouldExecute() {

    this.attributeInstance = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
    // Suppressed because attribute can be null
    //noinspection ConstantConditions
    return (this.attributeInstance != null);
  }

  @Override
  public void startExecuting() {

    EntityLivingBase attackTarget = this.taskOwner.getAttackTarget();

    if (attackTarget == null || this.taskOwner.getDistanceSq(attackTarget) > this.rangeSq) {
      this.attributeInstance.removeModifier(LUNGE_SPEED_BOOST_ID);

    } else {

      if (this.attributeInstance.getModifier(LUNGE_SPEED_BOOST_ID) == null) {
        AttributeModifier attributeModifier = new AttributeModifier(LUNGE_SPEED_BOOST_ID, "Lunge speed boost", this.speedModifier, 2);
        attributeModifier.setSaved(false);
        this.attributeInstance.applyModifier(attributeModifier);
      }
    }
  }

  @Override
  public boolean shouldContinueExecuting() {

    return false;
  }
}
