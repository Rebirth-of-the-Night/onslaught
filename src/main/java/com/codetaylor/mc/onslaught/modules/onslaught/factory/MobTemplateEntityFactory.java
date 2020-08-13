package com.codetaylor.mc.onslaught.modules.onslaught.factory;

import com.codetaylor.mc.onslaught.modules.onslaught.data.MobTemplate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Responsible for consuming a mob template and producing an entity.
 */
public class MobTemplateEntityFactory {

  public static final MobTemplateEntityFactory INSTANCE = new MobTemplateEntityFactory(
      new MobTemplateEntityEffectApplicator()
  );

  private final MobTemplateEntityEffectApplicator effectApplicator;

  private MobTemplateEntityFactory(MobTemplateEntityEffectApplicator effectApplicator) {

    this.effectApplicator = effectApplicator;
  }

  @Nullable
  public Entity create(MobTemplate template, World world) {

    NBTTagCompound tagCompound = template.nbt.copy();
    tagCompound.setString("id", template.id);
    Entity entity = EntityList.createEntityFromNBT(tagCompound, world);

    if (entity instanceof EntityLiving) {
      this.effectApplicator.applyEffects(template.effects, (EntityLiving) entity);
    }

    return entity;
  }
}
