package com.codetaylor.mc.onslaught.modules.onslaught.factory;

import com.codetaylor.mc.onslaught.modules.onslaught.data.MobTemplate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Responsible for consuming a mob template and producing an entity.
 */
public class MobTemplateEntityFactory {

  public Entity create(MobTemplate template, World world) {

    NBTTagCompound tagCompound = template.nbt.copy();
    tagCompound.setString("id", template.id);
    return EntityList.createEntityFromNBT(tagCompound, world);
  }
}
