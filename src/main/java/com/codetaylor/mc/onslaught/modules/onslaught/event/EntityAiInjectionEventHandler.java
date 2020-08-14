package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAiMiningInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityAiInjectionEventHandler {

  private final EntityAiMiningInjector digInjector;

  public EntityAiInjectionEventHandler(EntityAiMiningInjector digInjector) {

    this.digInjector = digInjector;
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

      this.digInjector.inject(entityLiving, customAiTag);
    }
  }

}
