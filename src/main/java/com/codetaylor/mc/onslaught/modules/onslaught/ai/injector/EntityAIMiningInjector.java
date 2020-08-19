package com.codetaylor.mc.onslaught.modules.onslaught.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIMining;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Responsible for injecting the AI mining task into entities with the tag.
 */
public class EntityAIMiningInjector {

  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_MINING)) {
      return;
    }

    entity.tasks.addTask(1, new EntityAIMining(entity));
  }
}