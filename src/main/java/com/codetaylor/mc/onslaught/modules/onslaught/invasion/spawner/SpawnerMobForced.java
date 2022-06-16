package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.MobTemplateEntityFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.SpawnSampler;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.template.mob.MobTemplate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Responsible for attempting a forced invasion spawn. */
public class SpawnerMobForced {

  private static final Logger LOGGER = LogManager.getLogger(SpawnerMobForced.class);

  private final Function<String, MobTemplate> mobTemplateFunction;
  private final MobTemplateEntityFactory mobTemplateEntityFactory;
  private final SpawnSampler spawnSampler;
  private final List<DeferredSpawnData> deferredSpawnDataList;
  private final IntSupplier forcedSpawnDelayTicksSupplier;

  public SpawnerMobForced(
      SpawnSampler spawnSampler,
      Function<String, MobTemplate> mobTemplateFunction,
      MobTemplateEntityFactory mobTemplateEntityFactory,
      List<DeferredSpawnData> deferredSpawnDataList,
      IntSupplier forcedSpawnDelayTicksSupplier) {

    this.mobTemplateFunction = mobTemplateFunction;
    this.mobTemplateEntityFactory = mobTemplateEntityFactory;
    this.spawnSampler = spawnSampler;
    this.deferredSpawnDataList = deferredSpawnDataList;
    this.forcedSpawnDelayTicksSupplier = forcedSpawnDelayTicksSupplier;
  }

  /** @return true if the mob was spawned */
  public boolean attemptSpawnMob(
      World world,
      BlockPos playerPos,
      UUID invasionUuid,
      UUID playerUuid,
      int waveIndex,
      int mobIndex,
      String mobTemplateId,
      InvasionPlayerData.InvasionData.SpawnData spawnData,
      InvasionTemplateWave.SecondaryMob secondaryMob) {

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

    InvasionPlayerData.InvasionData.SpawnData spawnDataCopy = spawnData.copy();
    spawnDataCopy.light = new int[] {0, 15};

    Vec3d spawnLocation = this.spawnSampler.getSpawnLocation(entity, playerPos, spawnDataCopy);

    if (spawnLocation == null) {

      if (ModuleOnslaughtConfig.DEBUG.INVASION_SPAWNERS) {
        String message = "Unable to find spawn location";
        ModOnslaught.LOG.fine(message);
        System.out.println(message);
      }

      return false;
    }

    DeferredSpawnData deferredSpawnData =
        new DeferredSpawnData(
            entity,
            world.provider.getDimension(),
            entity.getPosition(),
            invasionUuid,
            playerUuid,
            waveIndex,
            mobIndex,
            spawnDataCopy.type,
            secondaryMob,
            this.forcedSpawnDelayTicksSupplier.getAsInt());

    this.deferredSpawnDataList.add(deferredSpawnData);

    if (ModuleOnslaughtConfig.DEBUG.INVASION_SPAWNERS) {
      String message = "Created new deferred spawn data: " + deferredSpawnData.toString();
      ModOnslaught.LOG.fine(message);
      System.out.println(message);
    }

    return true;
  }
}
