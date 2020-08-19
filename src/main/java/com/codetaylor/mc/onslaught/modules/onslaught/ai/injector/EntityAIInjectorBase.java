package com.codetaylor.mc.onslaught.modules.onslaught.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

public abstract class EntityAIInjectorBase {

  public abstract void inject(EntityLiving entity, NBTTagCompound tag);

  protected int getPriority(NBTTagCompound aiTag, int defaultPriority) {

    return aiTag.hasKey(Tag.AI_PARAM_PRIORITY) ? aiTag.getInteger(Tag.AI_PARAM_PRIORITY) : defaultPriority;
  }
}
