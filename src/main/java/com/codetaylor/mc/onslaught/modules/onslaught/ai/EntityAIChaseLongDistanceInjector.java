package com.codetaylor.mc.onslaught.modules.onslaught.ai;

import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Responsible for injecting the AI player target task into entities with the tag.
 */
public class EntityAIChaseLongDistanceInjector {

  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!(entity instanceof EntityCreature)) {
      return;
    }

    if (!tag.hasKey(Tag.AI_PLAYER_TARGET)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_PLAYER_TARGET);

    double speed;

    if (aiTag.hasKey(Tag.AI_PARAM_SPEED)) {
      speed = aiTag.getDouble(Tag.AI_PARAM_SPEED);

    } else {
      speed = EntityAIChaseLongDistance.DEFAULT_SPEED;
    }

    entity.tasks.addTask(5, new EntityAIChaseLongDistance((EntityCreature) entity, speed));
  }
}