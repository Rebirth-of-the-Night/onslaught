package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.ai.DefaultPriority;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

/**
 * Responsible for tagging mobs with invasion data and applying the TargetPlayer
 * and ChaseLongDistance AI task NBT.
 */
public class EntityInvasionDataInjector {

  public void inject(EntityLiving entity, UUID uuid, int waveIndex, int mobIndex) {

    NBTTagCompound entityData = entity.getEntityData();

    if (!entityData.hasKey(Tag.ONSLAUGHT)) {
      entityData.setTag(Tag.ONSLAUGHT, new NBTTagCompound());
    }

    this.injectCustomAI(uuid, entityData);
    this.injectInvasionData(uuid, waveIndex, mobIndex, entityData);
  }

  private void injectInvasionData(UUID uuid, int waveIndex, int mobIndex, NBTTagCompound entityData) {

    NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

    NBTTagCompound tag = new NBTTagCompound();
    tag.setString(Tag.INVASION_UUID, uuid.toString());
    tag.setInteger(Tag.INVASION_WAVE_INDEX, waveIndex);
    tag.setInteger(Tag.INVASION_MOB_INDEX, mobIndex);
    modTag.setTag(Tag.INVASION_DATA, tag);
  }

  private void injectCustomAI(UUID uuid, NBTTagCompound entityData) {

    NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

    if (!modTag.hasKey(Tag.CUSTOM_AI)) {
      modTag.setTag(Tag.CUSTOM_AI, new NBTTagCompound());
    }

    NBTTagCompound customAiTag = modTag.getCompoundTag(Tag.CUSTOM_AI);

    this.injectTargetPlayer(uuid, customAiTag);
    this.injectChaseLongDistance(customAiTag);
  }

  private void injectChaseLongDistance(NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_CHASE_LONG_DISTANCE)) {
      tag.setTag(Tag.AI_CHASE_LONG_DISTANCE, new NBTTagCompound());
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_CHASE_LONG_DISTANCE);

    if (!aiTag.hasKey(Tag.AI_PARAM_PRIORITY)) {
      aiTag.setInteger(Tag.AI_PARAM_PRIORITY, DefaultPriority.CHASE_LONG_DISTANCE);
    }

    if (!aiTag.hasKey(Tag.AI_PARAM_SPEED)) {
      aiTag.setDouble(Tag.AI_PARAM_SPEED, ModuleOnslaughtConfig.CUSTOM_AI.LONG_DISTANCE_CHASE.DEFAULT_SPEED);
    }
  }

  private void injectTargetPlayer(UUID uuid, NBTTagCompound tag) {

    if (!tag.hasKey(Tag.AI_TARGET_PLAYER)) {
      tag.setTag(Tag.AI_TARGET_PLAYER, new NBTTagCompound());
    }

    NBTTagCompound aiTag = tag.getCompoundTag(Tag.AI_TARGET_PLAYER);

    if (!aiTag.hasKey(Tag.AI_PARAM_PRIORITY)) {
      aiTag.setInteger(Tag.AI_PARAM_PRIORITY, DefaultPriority.PLAYER_TARGET);
    }

    aiTag.setString(Tag.AI_PARAM_UUID, uuid.toString());
  }

}
