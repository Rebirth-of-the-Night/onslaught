package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.injector;

import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.EntityAIOffscreenTeleport;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

public class EntityAIOffscreenTeleportInjector extends EntityAIInjectorBase {
  @Override
  public void inject(EntityLiving entity, NBTTagCompound tag) {
    entity.tasks.addTask(-20, new EntityAIOffscreenTeleport(entity, 64, 32, true));
  }
}
