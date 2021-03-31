package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIAntiAir;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/** Responsible for injecting the AI anti-air task into entities with the tag. */
public class EntityAIAntiAirInjector extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_ANTI_AIR)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_ANTI_AIR);

    int priority = this.getPriority(aiTag, DefaultPriority.ANTI_AIR);
    int range =
        this.getInteger(
            aiTag, Tag.AI_PARAM_RANGE, ModuleOnslaughtConfig.CUSTOM_AI.ANTI_AIR.DEFAULT_RANGE);
    boolean sightRequired =
        this.getBoolean(
            aiTag,
            Tag.AI_PARAM_SIGHT_REQUIRED,
            ModuleOnslaughtConfig.CUSTOM_AI.ANTI_AIR.DEFAULT_SIGHT_REQUIRED);
    double motionY =
        this.getDouble(
            aiTag,
            Tag.AI_PARAM_MOTION_Y,
            ModuleOnslaughtConfig.CUSTOM_AI.ANTI_AIR.DEFAULT_MOTION_Y);

    entity.tasks.addTask(priority, new EntityAIAntiAir(entity, sightRequired, range, motionY));
  }
}
