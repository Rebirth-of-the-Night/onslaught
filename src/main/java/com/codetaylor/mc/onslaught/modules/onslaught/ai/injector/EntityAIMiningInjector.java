package com.codetaylor.mc.onslaught.modules.onslaught.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIMining;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Responsible for injecting the AI mining task into entities with the tag.
 */
public class EntityAIMiningInjector
    extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_MINING)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_MINING);

    int priority = this.getPriority(aiTag, DefaultPriority.MINING);
    int range = this.getInteger(aiTag, Tag.AI_PARAM_RANGE, EntityAIMining.DEFAULT_RANGE);
    double speedModifier = this.getDouble(aiTag, Tag.AI_PARAM_SPEED_MODIFIER, EntityAIMining.DEFAULT_SPEED_MODIFIER);

    entity.tasks.addTask(priority, new EntityAIMining(entity, range, speedModifier));
  }
}