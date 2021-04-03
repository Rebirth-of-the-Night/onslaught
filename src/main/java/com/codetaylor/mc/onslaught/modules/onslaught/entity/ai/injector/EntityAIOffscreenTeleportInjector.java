package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig.CustomAI.OffscreenTeleport;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIOffscreenTeleport;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

public class EntityAIOffscreenTeleportInjector extends EntityAIInjectorBase {
  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_OFFSCREEN_TELEPORT)) {
      return;
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_OFFSCREEN_TELEPORT);

    OffscreenTeleport config = ModuleOnslaughtConfig.CUSTOM_AI.OFFSCREEN_TELEPORT;

    int distance = getInteger(aiTag, Tag.AI_PARAM_TELE_THRESHOLD, config.DEFAULT_RANGE);
    float factor = (float)getDouble(aiTag, Tag.AI_PARAM_TELE_FACTOR, config.DEFAULT_FACTOR);
    boolean dimHopping = getBoolean(aiTag, Tag.AI_PARAM_DIM_HOP, config.DEFAULT_DIM_HOPPING);

    entity.tasks.addTask(DefaultPriority.OFFSCREEN_TELEPORT,
        new EntityAIOffscreenTeleport(
            entity, distance, factor, dimHopping));
  }
}
