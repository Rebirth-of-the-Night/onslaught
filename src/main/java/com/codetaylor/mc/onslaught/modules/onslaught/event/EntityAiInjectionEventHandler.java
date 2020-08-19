package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.ai.injector.EntityAIChaseLongDistanceInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.ai.injector.EntityAIMiningInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.ai.injector.EntityAIPlayerTargetInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityAiInjectionEventHandler {

  private final EntityAIPlayerTargetInjector playerTargetInjector;
  private final EntityAIChaseLongDistanceInjector chaseLongDistanceInjector;
  private final EntityAIMiningInjector miningInjector;

  public EntityAiInjectionEventHandler(
      EntityAIPlayerTargetInjector playerTargetInjector,
      EntityAIChaseLongDistanceInjector chaseLongDistanceInjector,
      EntityAIMiningInjector miningInjector
  ) {

    this.playerTargetInjector = playerTargetInjector;
    this.chaseLongDistanceInjector = chaseLongDistanceInjector;
    this.miningInjector = miningInjector;
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
    }
  }
}