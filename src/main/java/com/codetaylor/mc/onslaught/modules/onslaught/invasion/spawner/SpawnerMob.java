package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.MobTemplateEntityFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.SpawnSampler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Responsible for attempting an invasion spawn.
 */
public class SpawnerMob {

  private static final Logger LOGGER = LogManager.getLogger(SpawnerMob.class);

  private final SpawnSampler spawnSampler;
  private final Function<String, MobTemplate> mobTemplateFunction;
  private final MobTemplateEntityFactory mobTemplateEntityFactory;
  private final EntityInvasionDataInjector entityInvasionDataInjector;

  public SpawnerMob(
      SpawnSampler spawnSampler,
      Function<String, MobTemplate> mobTemplateFunction,
      MobTemplateEntityFactory mobTemplateEntityFactory,
      EntityInvasionDataInjector entityInvasionDataInjector
  ) {

    this.spawnSampler = spawnSampler;
    this.mobTemplateFunction = mobTemplateFunction;
    this.mobTemplateEntityFactory = mobTemplateEntityFactory;
    this.entityInvasionDataInjector = entityInvasionDataInjector;
  }

  /**
   * @return true if the mob was spawned
   */
  public boolean attemptSpawnMob(
      World world,
      BlockPos playerPos,
      UUID invasionUuid,
      UUID playerUuid,
      int waveIndex,
      int mobIndex,
      String mobTemplateId,
      InvasionPlayerData.InvasionData.SpawnData spawnData
  ) {

    MobTemplate mobTemplate = this.mobTemplateFunction.apply(mobTemplateId);

    if (mobTemplate == null) {
      String message = "Unknown mob template id: " + mobTemplateId;
      ModOnslaught.LOG.log(Level.SEVERE, message);
      LOGGER.error(message);
      return false;
    }

    EntityLiving entity = this.mobTemplateEntityFactory.create(mobTemplate, world);

    if (entity == null) {
      String message = "Unknown entity id: " + mobTemplate.id;
      ModOnslaught.LOG.log(Level.SEVERE, message);
      LOGGER.error(message);
      return false;
    }

    Vec3d spawnLocation = this.spawnSampler.getSpawnLocation(entity, playerPos, spawnData);

    if (spawnLocation == null) {

      if (ModuleOnslaughtConfig.DEBUG.INVASION_SPAWNERS) {
        String message = "Unable to find spawn location";
        ModOnslaught.LOG.fine(message);
        System.out.println(message);
      }

      return false;
    }

    // apply player target, chase long distance, and invasion data tags
    this.entityInvasionDataInjector.inject(entity, invasionUuid, playerUuid, waveIndex, mobIndex);

    if (!world.spawnEntity(entity)) {
      String message = "Unable to spawn entity: " + entity;
      ModOnslaught.LOG.log(Level.SEVERE, message);
      LOGGER.error(message);
      return false;
    }

    entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);

    if (ModuleOnslaughtConfig.DEBUG.INVASION_SPAWNERS) {
      String message = "Spawned entity: " + entity;
      ModOnslaught.LOG.fine(message);
      System.out.println(message);
    }

    return true;
  }
}
