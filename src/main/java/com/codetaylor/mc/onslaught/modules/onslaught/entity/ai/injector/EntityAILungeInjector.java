package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAILunge;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Responsible for injecting the AI lunge task into entities with the tag.
 */
public class EntityAILungeInjector
    extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_LUNGE)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_LUNGE);

    int priority = this.getPriority(aiTag, DefaultPriority.LUNGE);
    int range = this.getInteger(aiTag, Tag.AI_PARAM_RANGE, ModuleOnslaughtConfig.CUSTOM_AI.LUNGE.DEFAULT_RANGE);
    double speedModifier = this.getDouble(aiTag, Tag.AI_PARAM_SPEED_MODIFIER, ModuleOnslaughtConfig.CUSTOM_AI.LUNGE.DEFAULT_SPEED_MODIFIER);

    entity.tasks.addTask(priority, new EntityAILunge(entity, range, speedModifier));
  }
}