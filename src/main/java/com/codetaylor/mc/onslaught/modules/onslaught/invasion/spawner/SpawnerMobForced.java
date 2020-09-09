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

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Responsible for attempting a forced invasion spawn.
 */
public class SpawnerMobForced {

  private final Function<String, MobTemplate> mobTemplateFunction;
  private final MobTemplateEntityFactory mobTemplateEntityFactory;
  private final EntityInvasionDataInjector entityInvasionDataInjector;
  private final SpawnSampler spawnSampler;
  private final List<DeferredSpawnData> deferredSpawnDataList;

  public SpawnerMobForced(
      SpawnSampler spawnSampler,
      Function<String, MobTemplate> mobTemplateFunction,
      MobTemplateEntityFactory mobTemplateEntityFactory,
      EntityInvasionDataInjector entityInvasionDataInjector,
      List<DeferredSpawnData> deferredSpawnDataList
  ) {

    this.mobTemplateFunction = mobTemplateFunction;
    this.mobTemplateEntityFactory = mobTemplateEntityFactory;
    this.entityInvasionDataInjector = entityInvasionDataInjector;
    this.spawnSampler = spawnSampler;
    this.deferredSpawnDataList = deferredSpawnDataList;
  }

  /**
   * @return true if the mob was spawned
   */
  public boolean attemptSpawnMob(
      World world,
      BlockPos playerPos,
      UUID uuid,
      int waveIndex,
      int mobIndex,
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

    InvasionPlayerData.InvasionData.SpawnData spawnDataCopy = spawnData.copy();
    spawnDataCopy.light = new int[]{0, 15};

    Vec3d spawnLocation = this.spawnSampler.getSpawnLocation(entity, playerPos, spawnDataCopy);

    if (spawnLocation == null) {
      return false;
    }

    // TODO
    /*
    Place the deferred mob data into a collection.
    Create a class to reduce the time on each deferred data element.

    Create a class to spawn particles at each deferred data element's location.
    Create a class to manage nearby players' potion effects.
    Create a class to spawn a deferred mob when its element's timer expires.
      - Check that the player is alive and in the same dimension as the spawning mob.
    Create a class to cleanup elements and do player data bookkeeping when a chunk is unloaded.
      - Either this or we just clean them up and do the bookkeeping if they try to spawn in an unloaded chunk.
     */

    // apply player target, chase long distance, and invasion data tags
    this.entityInvasionDataInjector.inject(entity, uuid, waveIndex, mobIndex);

    this.deferredSpawnDataList.add(new DeferredSpawnData(
        entity,
        world.provider.getDimension(),
        entity.getPosition(),
        uuid,
        waveIndex,
        mobIndex,
        spawnDataCopy.type,
        ModuleOnslaughtConfig.INVASION.FORCED_SPAWN_DELAY_TICKS
    ));

    return true;
  }
}
