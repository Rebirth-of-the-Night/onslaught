package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIChaseLongDistance;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIChaseLongDistanceGhast;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;

/** Responsible for injecting the AI player target task into entities with the tag. */
public class EntityAIChaseLongDistanceInjector extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (entity.getClass() == EntitySlime.class) {
      return;
    }

    if (!tag.hasKey(Tag.AI_CHASE_LONG_DISTANCE)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_CHASE_LONG_DISTANCE);

    int priority = this.getPriority(aiTag, DefaultPriority.CHASE_LONG_DISTANCE);
    double speed =
        this.getDouble(
            aiTag,
            Tag.AI_PARAM_SPEED,
            ModuleOnslaughtConfig.CUSTOM_AI.LONG_DISTANCE_CHASE.DEFAULT_SPEED);

    if (entity.getClass() == EntityGhast.class) {
      entity.tasks.addTask(
          priority, new EntityAIChaseLongDistanceGhast((EntityGhast) entity, speed));

    } else {
      entity.tasks.addTask(priority, new EntityAIChaseLongDistance(entity, speed));
    }
  }
}
