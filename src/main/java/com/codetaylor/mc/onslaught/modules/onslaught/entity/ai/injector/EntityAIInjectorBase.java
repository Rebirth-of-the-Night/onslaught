package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Contains common methods used by all subclasses.
 */
public abstract class EntityAIInjectorBase {

  public abstract void inject(EntityLiving entity, NBTTagCompound tag);

  protected int getPriority(NBTTagCompound aiTag, int defaultPriority) {

    return aiTag.hasKey(Tag.AI_PARAM_PRIORITY) ? aiTag.getInteger(Tag.AI_PARAM_PRIORITY) : defaultPriority;
  }

  protected double getDouble(NBTTagCompound aiTag, String key, double defaultValue) {

    if (aiTag.hasKey(key)) {
      return aiTag.getDouble(key);
    }

    return defaultValue;
  }

  protected int getInteger(NBTTagCompound aiTag, String key, int defaultValue) {

    if (aiTag.hasKey(key)) {
      return aiTag.getInteger(key);
    }

    return defaultValue;
  }

  protected boolean getBoolean(NBTTagCompound aiTag, String key, boolean defaultValue) {

    if (aiTag.hasKey(key)) {
      return aiTag.getBoolean(key);
    }

    return defaultValue;
  }
}
