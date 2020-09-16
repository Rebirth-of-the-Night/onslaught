package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.UUID;

/**
 * Responsible for counting active mobs for a specific player, wave, and mob type.
 */
public class ActiveMobCounter {

  private final List<DeferredSpawnData> deferredSpawnDataList;

  public ActiveMobCounter(List<DeferredSpawnData> deferredSpawnDataList) {

    this.deferredSpawnDataList = deferredSpawnDataList;
  }

  public int countActiveMobs(List<Entity> entityList, UUID uuid, int waveIndex, int mobIndex) {

    int result = 0;

    for (Entity entity : entityList) {
      NBTTagCompound entityData = entity.getEntityData();

      if (!entityData.hasKey(Tag.ONSLAUGHT)) {
        continue;
      }

      NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

      if (modTag.hasKey(Tag.INVASION_DATA)) {
        NBTTagCompound tag = modTag.getCompoundTag(Tag.INVASION_DATA);
        String uuidData = tag.getString(Tag.INVASION_UUID);
        int waveIndexData = tag.getInteger(Tag.INVASION_WAVE_INDEX);
        int mobIndexData = tag.getInteger(Tag.INVASION_MOB_INDEX);

        if (waveIndex == waveIndexData
            && mobIndex == mobIndexData
            && uuid.toString().equals(uuidData)) {
          result += 1;
        }
      }
    }

    // Include deferred spawns in the count
    for (DeferredSpawnData deferredSpawnData : this.deferredSpawnDataList) {

      if (waveIndex == deferredSpawnData.getWaveIndex()
          && mobIndex == deferredSpawnData.getMobIndex()
          && uuid.equals(deferredSpawnData.getUuid())) {
        result += 1;
      }
    }

    return result;
  }
}