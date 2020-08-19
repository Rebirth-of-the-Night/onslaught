package com.codetaylor.mc.onslaught.modules.onslaught.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIChaseLongDistance;
import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIChaseLongDistanceGhast;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Responsible for injecting the AI player target task into entities with the tag.
 */
public class EntityAIChaseLongDistanceInjector
    extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (entity.getClass() == EntitySlime.class) {
      return;
    }

    if (!tag.hasKey(Tag.AI_CHASE_LONG_DISTANCE)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_CHASE_LONG_DISTANCE);

    int priority = this.getPriority(aiTag, EntityAIChaseLongDistance.DEFAULT_PRIORITY);

    double speed;

    if (aiTag.hasKey(Tag.AI_PARAM_SPEED)) {
      speed = aiTag.getDouble(Tag.AI_PARAM_SPEED);

    } else {
      speed = EntityAIChaseLongDistance.DEFAULT_SPEED;
    }

    if (entity.getClass() == EntityGhast.class) {
      entity.tasks.addTask(priority, new EntityAIChaseLongDistanceGhast((EntityGhast) entity, speed));

    } else {
      entity.tasks.addTask(priority, new EntityAIChaseLongDistance(entity, speed));
    }
  }
}