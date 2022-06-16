package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAICounterAttack;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/** Responsible for injecting the AI counterattack task into entities with the tag. */
public class EntityAICounterAttackInjector extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_COUNTER_ATTACK)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_COUNTER_ATTACK);

    int priority = this.getPriority(aiTag, DefaultPriority.COUNTER_ATTACK);
    float leapMotionXZ =
        (float)
            this.getDouble(
                aiTag,
                Tag.AI_PARAM_LEAP_MOTION_XZ,
                ModuleOnslaughtConfig.CUSTOM_AI.COUNTER_ATTACK.DEFAULT_LEAP_MOTION_XZ);
    float leapMotionY =
        (float)
            this.getDouble(
                aiTag,
                Tag.AI_PARAM_LEAP_MOTION_Y,
                ModuleOnslaughtConfig.CUSTOM_AI.COUNTER_ATTACK.DEFAULT_LEAP_MOTION_Y);
    float chance =
        (float)
            this.getDouble(
                aiTag,
                Tag.AI_PARAM_CHANCE,
                ModuleOnslaughtConfig.CUSTOM_AI.COUNTER_ATTACK.DEFAULT_CHANCE);
    float rangeMin =
        (float)
            this.getDouble(
                aiTag,
                Tag.AI_PARAM_RANGE_MIN,
                ModuleOnslaughtConfig.CUSTOM_AI.COUNTER_ATTACK.DEFAULT_RANGE[0]);
    float rangeMax =
        (float)
            this.getDouble(
                aiTag,
                Tag.AI_PARAM_RANGE_MAX,
                ModuleOnslaughtConfig.CUSTOM_AI.COUNTER_ATTACK.DEFAULT_RANGE[1]);

    entity.tasks.addTask(
        priority,
        new EntityAICounterAttack(entity, leapMotionXZ, leapMotionY, chance, rangeMin, rangeMax));
  }
}
