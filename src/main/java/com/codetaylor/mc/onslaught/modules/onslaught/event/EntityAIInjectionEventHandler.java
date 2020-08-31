package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.*;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Responsible for injecting the custom AI tasks into an entity when it spawns.
 */
public class EntityAIInjectionEventHandler {

  private final EntityAIPlayerTargetInjector playerTargetInjector;
  private final EntityAIChaseLongDistanceInjector chaseLongDistanceInjector;
  private final EntityAIMiningInjector miningInjector;
  private final EntityAIAttackMeleeInjector attackMeleeInjector;
  private final EntityAICounterAttackInjector counterAttackInjector;
  private final EntityAIExplodeWhenStuckInjector explodeWhenStuckInjector;
  private final EntityAILungeInjector lungeInjector;
  private final EntityAIAntiAirInjector antiAirInjector;

  public EntityAIInjectionEventHandler(
      EntityAIPlayerTargetInjector playerTargetInjector,
      EntityAIChaseLongDistanceInjector chaseLongDistanceInjector,
      EntityAIMiningInjector miningInjector,
      EntityAIAttackMeleeInjector attackMeleeInjector,
      EntityAICounterAttackInjector counterAttackInjector,
      EntityAIExplodeWhenStuckInjector explodeWhenStuckInjector,
      EntityAILungeInjector lungeInjector,
      EntityAIAntiAirInjector antiAirInjector
  ) {

    this.playerTargetInjector = playerTargetInjector;
    this.chaseLongDistanceInjector = chaseLongDistanceInjector;
    this.miningInjector = miningInjector;
    this.attackMeleeInjector = attackMeleeInjector;
    this.counterAttackInjector = counterAttackInjector;
    this.explodeWhenStuckInjector = explodeWhenStuckInjector;
    this.lungeInjector = lungeInjector;
    this.antiAirInjector = antiAirInjector;
  }

  @SubscribeEvent
  public void on(EntityJoinWorldEvent event) {

    if (event.getWorld().isRemote) {
      return;
    }

    Entity entity = event.getEntity();

    if (entity instanceof EntityLiving) {
      EntityLiving entityLiving = (EntityLiving) entity;

      NBTTagCompound entityData = entityLiving.getEntityData();

      if (!entityData.hasKey(Tag.ONSLAUGHT)) {
        return;
      }

      NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

      if (!modTag.hasKey(Tag.CUSTOM_AI)) {
        return;
      }

      NBTTagCompound customAiTag = modTag.getCompoundTag(Tag.CUSTOM_AI);

      this.playerTargetInjector.inject(entityLiving, customAiTag);
      this.chaseLongDistanceInjector.inject(entityLiving, customAiTag);
      this.miningInjector.inject(entityLiving, customAiTag);
      this.attackMeleeInjector.inject(entityLiving, customAiTag);
      this.counterAttackInjector.inject(entityLiving, customAiTag);
      this.explodeWhenStuckInjector.inject(entityLiving, customAiTag);
      this.lungeInjector.inject(entityLiving, customAiTag);
      this.antiAirInjector.inject(entityLiving, customAiTag);
    }
  }
}