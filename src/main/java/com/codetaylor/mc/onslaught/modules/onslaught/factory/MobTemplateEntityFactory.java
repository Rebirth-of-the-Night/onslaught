package com.codetaylor.mc.onslaught.modules.onslaught.factory;

import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
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

  private final MobTemplateEntityFactoryEffectApplicator effectApplicator;
  private final MobTemplateEntityFactoryLootTableApplicator lootTableApplicator;

  public MobTemplateEntityFactory(
      MobTemplateEntityFactoryEffectApplicator effectApplicator,
      MobTemplateEntityFactoryLootTableApplicator lootTableApplicator
  ) {

    this.effectApplicator = effectApplicator;
    this.lootTableApplicator = lootTableApplicator;
  }

  @Nullable
  public Entity create(MobTemplate template, World world) {

    NBTTagCompound tagCompound = template.nbt.copy();
    tagCompound.setString("id", template.id);
    Entity entity = EntityList.createEntityFromNBT(tagCompound, world);

    if (entity instanceof EntityLiving) {
      this.effectApplicator.apply(template.effects, (EntityLiving) entity);
    }

    this.lootTableApplicator.apply(template.extraLootTables, entity);

    return entity;
  }
}
