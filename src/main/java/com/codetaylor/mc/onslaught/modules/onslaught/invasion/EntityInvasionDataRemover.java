package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.MethodHandleHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Responsible for removing invasion data from the given entity and setting its
 * persistence flag to false.
 */
public class EntityInvasionDataRemover
    implements Consumer<EntityLiving> {

  private static final MethodHandle entityLiving$persistenceRequired;

  static {
    /*
    MC 1.12: net/minecraft/entity/EntityLiving.persistenceRequired
    Name: bA => field_82179_bU => persistenceRequired
    Comment: Whether this entity should NOT despawn.
    Side: BOTH
    AT: public net.minecraft.entity.EntityLiving field_82179_bU # persistenceRequired
     */
    entityLiving$persistenceRequired = MethodHandleHelper.unreflectSetter(World.class, "field_82179_bU");
  }

  @Override
  public void accept(EntityLiving entity) {

    NBTTagCompound entityData = entity.getEntityData();

    if (!entityData.hasKey(Tag.ONSLAUGHT)) {
      return;
    }

    NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

    modTag.removeTag(Tag.INVASION_DATA);

    if (entityLiving$persistenceRequired != null) {

      try {
        entityLiving$persistenceRequired.invokeExact(entity, false);

      } catch (Throwable throwable) {
        ModOnslaught.LOG.log(Level.SEVERE, "Error invoking unreflected setter for field_82179_bU");
        ModOnslaught.LOG.log(Level.SEVERE, throwable.getMessage(), throwable);
      }
    }
  }
}