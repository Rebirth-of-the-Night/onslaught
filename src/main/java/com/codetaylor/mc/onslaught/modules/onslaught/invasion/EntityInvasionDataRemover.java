package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Responsible for removing invasion data from the given entity and setting its persistence flag to
 * false.
 */
public class EntityInvasionDataRemover implements Consumer<EntityLiving> {

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
      entity.persistenceRequired = false;
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

    synchronized (tasks.executingTaskEntries) {
      for (EntityAIBase entityAIBase : tasksToRemove) {
        tasks.removeTask(entityAIBase);
      }
    }
  }
}
