package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIMining;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/** Responsible for injecting the AI mining task into entities with the tag. */
public class EntityAIMiningInjector extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_MINING)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_MINING);

    int priority = this.getPriority(aiTag, DefaultPriority.MINING);
    int range =
        this.getInteger(
            aiTag, Tag.AI_PARAM_RANGE, ModuleOnslaughtConfig.CUSTOM_AI.MINING.DEFAULT_RANGE);
    double speedModifier =
        this.getDouble(
            aiTag,
            Tag.AI_PARAM_SPEED_MODIFIER,
            ModuleOnslaughtConfig.CUSTOM_AI.MINING.DEFAULT_SPEED_MODIFIER);

    entity.tasks.addTask(priority, new EntityAIMining(entity, range, speedModifier));
  }
}
