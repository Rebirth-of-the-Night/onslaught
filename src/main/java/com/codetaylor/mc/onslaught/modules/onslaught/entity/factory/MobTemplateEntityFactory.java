package com.codetaylor.mc.onslaught.modules.onslaught.entity.factory;

import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
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
  public EntityLiving create(MobTemplate template, World world) {

    NBTTagCompound tagCompound = template.nbt.copy();
    tagCompound.setString("id", template.id);

    // Ensure that the entity does not despawn normally.
    if (!tagCompound.hasKey("PersistenceRequired")) {
      tagCompound.setBoolean("PersistenceRequired", true);
    }

    Entity entity = EntityList.createEntityFromNBT(tagCompound, world);

    if (!(entity instanceof EntityLiving)) {
      return null;
    }

    // Apply additional effects.
    this.effectApplicator.apply(template.effects, (EntityLiving) entity);

    // Apply additional loot tables.
    this.lootTableApplicator.apply(template.extraLootTables, entity);

    // Ensure that the entity can path away from its home restriction.
    if (entity instanceof EntityCreature) {
      boolean detachHome = (!tagCompound.hasKey(Tag.DETACH_HOME) || tagCompound.getBoolean(Tag.DETACH_HOME));

      if (detachHome) {
        ((EntityCreature) entity).detachHome();
      }
    }

    return (EntityLiving) entity;
  }
}
