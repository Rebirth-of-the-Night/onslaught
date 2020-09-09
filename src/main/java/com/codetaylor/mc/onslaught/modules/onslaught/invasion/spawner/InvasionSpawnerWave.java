package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionSpawnDataConverter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
public class InvasionSpawnerWave {

  private static final int MAX_ALLOWED_SPAWN_TIME_MS = 500;

  private final InvasionSpawnDataConverter invasionSpawnDataConverter;
  private final InvasionSpawnerMob invasionSpawnerMob;
  private final InvasionSpawnerMobForced invasionSpawnerMobForced;

  public InvasionSpawnerWave(
      InvasionSpawnDataConverter invasionSpawnDataConverter,
      InvasionSpawnerMob invasionSpawnerMob,
      InvasionSpawnerMobForced invasionSpawnerMobForced
  ) {

    this.invasionSpawnDataConverter = invasionSpawnDataConverter;
    this.invasionSpawnerMob = invasionSpawnerMob;
    this.invasionSpawnerMobForced = invasionSpawnerMobForced;
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

    for (int mobIndex = 0; mobIndex < waveData.mobDataList.size(); mobIndex++) {
      InvasionPlayerData.InvasionData.MobData mobData = waveData.mobDataList.get(mobIndex);

      while (mobData.remainingSpawnCount > 0) {

        // Try to spawn mob normally
        // Try to force spawn (magic spawns)
        // Try to spawn secondary mob normally

        World world = player.world;
        BlockPos position = player.getPosition();
        UUID uuid = player.getUniqueID();

        if (this.invasionSpawnerMob.attemptSpawnMob(world, position, uuid, waveIndex, mobIndex, mobData.id, mobData.spawnData)
            || (mobData.spawnData.force && this.invasionSpawnerMobForced.attemptSpawnMob(world, position, uuid, waveIndex, mobIndex, mobData.id, mobData.spawnData))
            || this.invasionSpawnerMob.attemptSpawnMob(world, position, uuid, waveIndex, mobIndex, secondaryMob.id, this.invasionSpawnDataConverter.convert(secondaryMob.spawn))) {

          mobData.remainingSpawnCount -= 1;

          if (System.currentTimeMillis() - startTimestamp > MAX_ALLOWED_SPAWN_TIME_MS) {
            return true; // Stop spawning
          }
        }
      }
    }

    return false; // Continue spawning
  }
}