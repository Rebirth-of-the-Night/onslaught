package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionSpawnDataConverter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Responsible for attempting to spawn an invasion wave for the given player.
 * <p>
 * Short-circuits if the time spent spawning exceeds the allotted maximum.
 * <p>
 * Attempts in this order:
 * - Try to spawn mob normally
 * - Try to force spawn (magic spawns)
 * - Try to spawn secondary mob normally
 */
public class SpawnerWave {

  private static final int MAX_ALLOWED_SPAWN_TIME_MS = 500;

  private final InvasionSpawnDataConverter invasionSpawnDataConverter;
  private final SpawnerMob spawnerMob;
  private final SpawnerMobForced spawnerMobForced;
  private final List<DeferredSpawnData> deferredSpawnDataList;

  public SpawnerWave(
      InvasionSpawnDataConverter invasionSpawnDataConverter,
      SpawnerMob spawnerMob,
      SpawnerMobForced spawnerMobForced,
      List<DeferredSpawnData> deferredSpawnDataList
  ) {

    this.invasionSpawnDataConverter = invasionSpawnDataConverter;
    this.spawnerMob = spawnerMob;
    this.spawnerMobForced = spawnerMobForced;
    this.deferredSpawnDataList = deferredSpawnDataList;
  }

  /**
   * @return true to stop the spawn loop
   */
  public boolean attemptSpawnWave(
      long startTimestamp,
      EntityPlayerMP player,
      int waveIndex,
      InvasionPlayerData.InvasionData.WaveData waveData,
      InvasionTemplateWave.SecondaryMob secondaryMob
  ) {

    List<InvasionPlayerData.InvasionData.MobData> mobDataList = waveData.getMobDataList();

    for (int mobIndex = 0; mobIndex < mobDataList.size(); mobIndex++) {
      InvasionPlayerData.InvasionData.MobData mobData = mobDataList.get(mobIndex);

      if (mobData.getKilledCount() >= mobData.getTotalCount()) {
        continue;
      }

      World world = player.world;
      BlockPos position = player.getPosition();
      UUID uuid = player.getUniqueID();
      InvasionPlayerData.InvasionData.SpawnData spawnData = mobData.getSpawnData();

      int activeMobs = this.countActiveMobs(world.loadedEntityList, uuid, waveIndex, mobIndex);
      int remainingMobs = mobData.getTotalCount() - mobData.getKilledCount() - activeMobs;

      while (remainingMobs > 0) {

        // Try to spawn mob normally
        // Try to force spawn (magic spawns)
        // Try to spawn secondary mob normally

        if (this.spawnerMob.attemptSpawnMob(world, position, uuid, waveIndex, mobIndex, mobData.getMobTemplateId(), spawnData)
            || (spawnData.force && this.spawnerMobForced.attemptSpawnMob(world, position, uuid, waveIndex, mobIndex, mobData.getMobTemplateId(), spawnData, secondaryMob))
            || this.spawnerMob.attemptSpawnMob(world, position, uuid, waveIndex, mobIndex, secondaryMob.id, this.invasionSpawnDataConverter.convert(secondaryMob.spawn))) {

          remainingMobs -= 1;

          if (System.currentTimeMillis() - startTimestamp > MAX_ALLOWED_SPAWN_TIME_MS) {
            return true; // Stop spawning
          }
        }
      }
    }

    return false; // Continue spawning
  }

  private int countActiveMobs(List<Entity> entityList, UUID uuid, int waveIndex, int mobIndex) {

    int result = 0;

    for (Entity entity : entityList) {
      NBTTagCompound entityData = entity.getEntityData();

      if (entityData.hasKey(Tag.INVASION_DATA)) {
        NBTTagCompound tag = entityData.getCompoundTag(Tag.INVASION_DATA);
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