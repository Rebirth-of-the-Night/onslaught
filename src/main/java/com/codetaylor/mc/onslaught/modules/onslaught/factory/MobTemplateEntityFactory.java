package com.codetaylor.mc.onslaught.modules.onslaught.factory;

import com.codetaylor.mc.onslaught.modules.onslaught.data.MobTemplate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Responsible for consuming a mob template and producing an entity.
 */
public class MobTemplateEntityFactory {

  private final MobTemplate template;

  public MobTemplateEntityFactory(MobTemplate template) {

    this.template = template;
  }

  @Nullable
  public Entity create(World world) {

    NBTTagCompound tagCompound = this.template.nbt.copy();
    tagCompound.setString("id", this.template.id);
    return EntityList.createEntityFromNBT(tagCompound, world);
  }
}
