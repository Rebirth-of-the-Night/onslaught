package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector.EntityAIInjectorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Responsible for injecting the custom AI tasks into an entity when it spawns. */
public class EntityAIInjectionEventHandler {

  private final EntityAIInjectorBase[] injectors;

  public EntityAIInjectionEventHandler(EntityAIInjectorBase[] injectors) {

    this.injectors = injectors;
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

      for (EntityAIInjectorBase injector : this.injectors) {
        injector.inject(entityLiving, customAiTag);
      }
    }
  }
}
