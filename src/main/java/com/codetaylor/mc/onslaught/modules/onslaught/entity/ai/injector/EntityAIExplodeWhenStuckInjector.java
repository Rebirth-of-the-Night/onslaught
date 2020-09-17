package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIExplodeWhenStuck;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Responsible for injecting the AI explode when stuck task into entities with the tag.
 */
public class EntityAIExplodeWhenStuckInjector
    extends EntityAIInjectorBase {

  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_EXPLODE_WHEN_STUCK)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_EXPLODE_WHEN_STUCK);

    int priority = this.getPriority(aiTag, DefaultPriority.EXPLODE_WHEN_STUCK);
    ModuleOnslaughtConfig.CustomAI.ExplodeWhenStuck config = ModuleOnslaughtConfig.CUSTOM_AI.EXPLODE_WHEN_STUCK;
    boolean sightRequired = this.getBoolean(aiTag, Tag.AI_PARAM_SIGHT_REQUIRED, config.DEFAULT_SIGHT_REQUIRED);
    boolean rangeRequired = this.getBoolean(aiTag, Tag.AI_PARAM_RANGE_REQUIRED, config.DEFAULT_RANGE_REQUIRED);
    float rangeMin = (float) this.getDouble(aiTag, Tag.AI_PARAM_RANGE_MIN, config.DEFAULT_RANGE[0]);
    float rangeMax = (float) this.getDouble(aiTag, Tag.AI_PARAM_RANGE_MAX, config.DEFAULT_RANGE[1]);
    int explosionDelayTicks = this.getInteger(aiTag, Tag.AI_PARAM_EXPLOSION_DELAY_TICKS, config.DEFAULT_EXPLOSION_DELAY_TICKS);
    float explosionStrength = (float) this.getDouble(aiTag, Tag.AI_PARAM_EXPLOSION_STRENGTH, config.DEFAULT_EXPLOSION_STRENGTH);
    boolean explosionCausesFire = this.getBoolean(aiTag, Tag.AI_PARAM_EXPLOSION_CAUSES_FIRE, config.DEFAULT_EXPLOSION_CAUSES_FIRE);
    boolean explosionDamaging = this.getBoolean(aiTag, Tag.AI_PARAM_EXPLOSION_DAMAGING, config.DEFAULT_EXPLOSION_DAMAGING);

    entity.tasks.addTask(priority, new EntityAIExplodeWhenStuck(entity, sightRequired, rangeRequired, rangeMin, rangeMax, explosionDelayTicks, explosionStrength, explosionCausesFire, explosionDamaging));
  }
}