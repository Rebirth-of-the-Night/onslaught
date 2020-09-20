package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.MethodHandleHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;
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
    entityLiving$persistenceRequired = MethodHandleHelper.unreflectSetter(EntityLiving.class, "field_82179_bU");
  }

  private final Class<?>[] toRemove;

  public EntityInvasionDataRemover(Class<?>[] toRemove) {

    this.toRemove = toRemove;
  }

  @Override
  public void accept(EntityLiving entity) {

    NBTTagCompound entityData = entity.getEntityData();

    if (!entityData.hasKey(Tag.ONSLAUGHT)) {
      return;
    }

    NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

    if (modTag.hasKey(Tag.INVASION_DATA)) {

      modTag.removeTag(Tag.INVASION_DATA);

      if (modTag.hasKey(Tag.CUSTOM_AI)) {
        NBTTagCompound customAiTag = modTag.getCompoundTag(Tag.CUSTOM_AI);

        customAiTag.removeTag(Tag.AI_TARGET_PLAYER);
        customAiTag.removeTag(Tag.AI_CHASE_LONG_DISTANCE);

        this.removeTasks(entity.tasks);
        this.removeTasks(entity.targetTasks);
      }

      // Remove persistence
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

  public void removeTasks(EntityAITasks tasks) {

    List<EntityAIBase> tasksToRemove = new ArrayList<>(tasks.taskEntries.size());

    for (EntityAITasks.EntityAITaskEntry taskEntry : tasks.taskEntries) {

      for (Class<?> aClass : this.toRemove) {

        if (aClass.isAssignableFrom(taskEntry.action.getClass())) {
          tasksToRemove.add(taskEntry.action);
        }
      }
    }

    for (EntityAIBase entityAIBase : tasksToRemove) {
      tasks.removeTask(entityAIBase);
    }
  }
}