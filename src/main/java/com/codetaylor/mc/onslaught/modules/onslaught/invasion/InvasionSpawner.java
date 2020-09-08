package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.MobTemplateEntityFactory;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;

public class InvasionSpawner {

  private static final int MAX_ALLOWED_SPAWN_TIME_MS = 500;

  private final InvasionSpawnSampler invasionSpawnSampler;
  private final Function<String, InvasionTemplate> invasionTemplateFunction;
  private final Function<String, MobTemplate> mobTemplateFunction;
  private final MobTemplateEntityFactory mobTemplateEntityFactory;
  private final InvasionSpawnNBTInjector invasionSpawnNBTInjector;
  private final InvasionFactorySpawnData invasionFactorySpawnData;

  public InvasionSpawner(
      InvasionSpawnSampler invasionSpawnSampler,
      Function<String, InvasionTemplate> invasionTemplateFunction,
      Function<String, MobTemplate> mobTemplateFunction,
      MobTemplateEntityFactory mobTemplateEntityFactory,
      InvasionSpawnNBTInjector invasionSpawnNBTInjector,
      InvasionFactorySpawnData invasionFactorySpawnData
  ) {

    this.invasionSpawnSampler = invasionSpawnSampler;
    this.invasionTemplateFunction = invasionTemplateFunction;
    this.mobTemplateFunction = mobTemplateFunction;
    this.mobTemplateEntityFactory = mobTemplateEntityFactory;
    this.invasionSpawnNBTInjector = invasionSpawnNBTInjector;
    this.invasionFactorySpawnData = invasionFactorySpawnData;
  }

  public void process(
      Supplier<List<EntityPlayerMP>> playerListSupplier
  ) {

    long start = System.currentTimeMillis();
    List<EntityPlayerMP> playerList = playerListSupplier.get();

    for (EntityPlayerMP player : playerList) {
      IInvasionPlayerData data = CapabilityInvasion.get(player);

      if (data.getInvasionState() != IInvasionPlayerData.EnumInvasionState.Active) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      // If the invasion state is active, the invasion data should never be null.
      if (invasionData == null) {
        continue;
      }

      InvasionTemplate invasionTemplate = this.invasionTemplateFunction.apply(invasionData.id);

      if (invasionTemplate == null) {
        // If this happens, the invasion id has changed since the invasion data was created.
        // Nullify the invasion to flag it as complete.
        // The state change processors will clean this up.
        data.setInvasionData(null);
        continue;
      }

      List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.waveDataList;

      for (int waveIndex = 0; waveIndex < waveDataList.size(); waveIndex++) {
        InvasionPlayerData.InvasionData.WaveData waveData = waveDataList.get(waveIndex);
        InvasionTemplateWave templateWave = invasionTemplate.waves[waveIndex];

        if (waveData.delayTicks <= 0) {

          if (this.attemptSpawnWave(start, player, waveData, templateWave.secondaryMob)) {
            return;
          }
        }
      }
    }
  }

  /**
   * @return true to stop the spawn loop
   */
  private boolean attemptSpawnWave(
      long start,
      EntityPlayerMP player,
      InvasionPlayerData.InvasionData.WaveData waveData,
      InvasionTemplateWave.SecondaryMob secondaryMob
  ) {

    for (InvasionPlayerData.InvasionData.MobData mobData : waveData.mobDataList) {

      while (mobData.remainingSpawnCount > 0) {

        // Try to spawn mob normally
        // TODO: Try to force spawn (magic spawns)
        // Try to spawn secondary mob normally

        World world = player.world;
        BlockPos position = player.getPosition();
        UUID uuid = player.getUniqueID();

        if (this.attemptSpawnMob(world, position, uuid, mobData.id, mobData.spawnData)
            || (mobData.spawnData.force && this.attemptSpawnMobForced(world, position, uuid, mobData.id, mobData.spawnData))
            || this.attemptSpawnMob(world, position, uuid, secondaryMob.id, this.invasionFactorySpawnData.create(secondaryMob.spawn))) {

          mobData.remainingSpawnCount -= 1;

          if (System.currentTimeMillis() - start > MAX_ALLOWED_SPAWN_TIME_MS) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * @return true if the mob was spawned
   */
  private boolean attemptSpawnMobForced(
      World world,
      BlockPos playerPos,
      UUID uuid,
      String mobTemplateId,
      InvasionPlayerData.InvasionData.SpawnData spawnData
  ) {

    // TODO

    return false;
  }

  /**
   * @return true if the mob was spawned
   */
  private boolean attemptSpawnMob(
      World world,
      BlockPos playerPos,
      UUID uuid,
      String mobTemplateId,
      InvasionPlayerData.InvasionData.SpawnData spawnData
  ) {

    MobTemplate mobTemplate = this.mobTemplateFunction.apply(mobTemplateId);

    if (mobTemplate == null) {
      ModOnslaught.LOG.log(Level.SEVERE, "Unknown mob template id: " + mobTemplateId);
      return false;
    }

    EntityLiving entity = this.mobTemplateEntityFactory.create(mobTemplate, world);

    if (entity == null) {
      ModOnslaught.LOG.log(Level.SEVERE, "Unknown entity id: " + mobTemplate.id);
      return false;
    }

    Vec3d spawnLocation = this.invasionSpawnSampler.getSpawnLocation(entity, playerPos, spawnData);

    if (spawnLocation == null) {
      return false;
    }

    // apply player target and LDPF tasks via NBT
    this.invasionSpawnNBTInjector.inject(entity, uuid);

    if (!world.spawnEntity(entity)) {
      return false;
    }

    entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);

    return true;
  }

}
