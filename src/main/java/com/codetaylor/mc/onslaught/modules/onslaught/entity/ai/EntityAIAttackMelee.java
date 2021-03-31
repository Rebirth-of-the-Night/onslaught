package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

import javax.annotation.Nonnull;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

/** Responsible for allowing passive entities to attack. */
public class EntityAIAttackMelee extends net.minecraft.entity.ai.EntityAIAttackMelee {

  private final float attackDamage;

  public EntityAIAttackMelee(
      EntityCreature creature, double speed, float attackDamage, boolean useLongMemory) {

    super(creature, speed, useLongMemory);
    this.attackDamage = attackDamage;
  }

  @Override
  protected void checkAndPerformAttack(@Nonnull EntityLivingBase entity, double distance) {

    double d0 = this.getAttackReachSqr(entity);

    if (distance <= d0 && this.attackTick <= 0) {
      this.attackTick = 20;
      this.attacker.swingArm(EnumHand.MAIN_HAND);
      this.attackEntityAsMob(this.attacker, entity, this.attackDamage);
    }
  }

  /** @see net.minecraft.entity.monster.EntityMob#attackEntityAsMob(Entity) */
  private void attackEntityAsMob(
      EntityCreature entityAttacker, @Nonnull EntityLivingBase entityTarget, float attackDamage) {

    int knockbackModifier = 0;

    attackDamage +=
        EnchantmentHelper.getModifierForCreature(
            entityAttacker.getHeldItemMainhand(), entityTarget.getCreatureAttribute());
    knockbackModifier += EnchantmentHelper.getKnockbackModifier(entityAttacker);

    boolean wasAttacked =
        entityTarget.attackEntityFrom(DamageSource.causeMobDamage(entityAttacker), attackDamage);

    if (wasAttacked) {

      if (knockbackModifier > 0) {
        entityTarget.knockBack(
            entityAttacker,
            (float) knockbackModifier * 0.5f,
            MathHelper.sin(entityAttacker.rotationYaw * 0.017453292f),
            -MathHelper.cos(entityAttacker.rotationYaw * 0.017453292f));
        entityAttacker.motionX *= 0.6;
        entityAttacker.motionZ *= 0.6;
      }

      int fireAspectModifier = EnchantmentHelper.getFireAspectModifier(entityAttacker);

      if (fireAspectModifier > 0) {
        entityTarget.setFire(fireAspectModifier * 4);
      }

      if (entityTarget instanceof EntityPlayer) {
        EntityPlayer entityplayer = (EntityPlayer) entityTarget;
        ItemStack heldItem = entityAttacker.getHeldItemMainhand();
        ItemStack activeItem =
            entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

        if (!heldItem.isEmpty()
            && !activeItem.isEmpty()
            && heldItem
                .getItem()
                .canDisableShield(heldItem, activeItem, entityplayer, entityAttacker)
            && activeItem.getItem().isShield(activeItem, entityplayer)) {
          float f1 =
              0.25f + (float) EnchantmentHelper.getEfficiencyModifier(entityAttacker) * 0.05f;

          if (entityAttacker.getRNG().nextFloat() < f1) {
            entityplayer.getCooldownTracker().setCooldown(activeItem.getItem(), 100);
            entityAttacker.world.setEntityState(entityplayer, (byte) 30);
          }
        }
      }

      this.applyEnchantments(entityAttacker, entityTarget);
    }
  }

  /** @see Entity#applyEnchantments(EntityLivingBase, Entity) */
  @SuppressWarnings("JavadocReference")
  protected void applyEnchantments(EntityLivingBase entityAttacker, Entity entityTarget) {

    if (entityTarget instanceof EntityLivingBase) {
      EnchantmentHelper.applyThornEnchantments((EntityLivingBase) entityTarget, entityAttacker);
    }

    EnchantmentHelper.applyArthropodEnchantments(entityAttacker, entityTarget);
  }
}
