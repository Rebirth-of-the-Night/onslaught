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

  public int countActiveMobs(List<Entity> entityList, UUID invasionUuid, UUID playerUuid, int waveIndex, int mobIndex) {

    int result = 0;
    String playerUuidString = playerUuid.toString();
    String invasionUuidString = invasionUuid.toString();

    for (Entity entity : entityList) {
      NBTTagCompound entityData = entity.getEntityData();

      if (!entityData.hasKey(Tag.ONSLAUGHT)) {
        continue;
      }

      NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

      if (modTag.hasKey(Tag.INVASION_DATA)) {
        NBTTagCompound tag = modTag.getCompoundTag(Tag.INVASION_DATA);
        String invasionUuidData = tag.getString(Tag.INVASION_UUID);
        String playerUuidData = tag.getString(Tag.INVASION_PLAYER_UUID);
        int waveIndexData = tag.getInteger(Tag.INVASION_WAVE_INDEX);
        int mobIndexData = tag.getInteger(Tag.INVASION_MOB_INDEX);

        if (waveIndex == waveIndexData
            && mobIndex == mobIndexData
            && playerUuidString.equals(playerUuidData)
            && invasionUuidString.equals(invasionUuidData)) {
          result += 1;
        }
      }
    }

    // Include deferred spawns in the count
    for (DeferredSpawnData deferredSpawnData : this.deferredSpawnDataList) {

      if (waveIndex == deferredSpawnData.getWaveIndex()
          && mobIndex == deferredSpawnData.getMobIndex()
          && playerUuid.equals(deferredSpawnData.getPlayerUuid())
          && invasionUuid.equals(deferredSpawnData.getInvasionUuid())) {
        result += 1;
      }
    }

    return result;
  }
}