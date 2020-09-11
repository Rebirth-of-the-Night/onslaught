package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.entity.factory.MobTemplateEntityFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionSpawnDataConverter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.sampler.predicate.SpawnPredicateFactory;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Responsible for spawning a deferred mob when its delay timer is expired.
 */
public class DeferredSpawner
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final EntityInvasionDataInjector entityInvasionDataInjector;
  private final SpawnPredicateFactory spawnPredicateFactory;
  private final InvasionSpawnDataConverter invasionSpawnDataConverter;
  private final Function<String, MobTemplate> mobTemplateFunction;
  private final MobTemplateEntityFactory mobTemplateEntityFactory;
  private final List<DeferredSpawnData> deferredSpawnDataList;

  public DeferredSpawner(
      EntityInvasionDataInjector entityInvasionDataInjector,
      SpawnPredicateFactory spawnPredicateFactory,
      InvasionSpawnDataConverter invasionSpawnDataConverter,
      Function<String, MobTemplate> mobTemplateFunction,
      MobTemplateEntityFactory mobTemplateEntityFactory,
      List<DeferredSpawnData> deferredSpawnDataList
  ) {

    this.entityInvasionDataInjector = entityInvasionDataInjector;
    this.spawnPredicateFactory = spawnPredicateFactory;
    this.invasionSpawnDataConverter = invasionSpawnDataConverter;
    this.mobTemplateFunction = mobTemplateFunction;
    this.mobTemplateEntityFactory = mobTemplateEntityFactory;
    this.deferredSpawnDataList = deferredSpawnDataList;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    if (this.deferredSpawnDataList.isEmpty()) {
      return;
    }

    for (int i = this.deferredSpawnDataList.size() - 1; i >= 0; i--) {
      DeferredSpawnData deferredSpawnData = this.deferredSpawnDataList.get(i);
      deferredSpawnData.setTicksRemaining(deferredSpawnData.getTicksRemaining() - updateIntervalTicks);

      if (deferredSpawnData.getTicksRemaining() <= 0) {
        EntityPlayerMP player = playerList.getPlayerByUUID(deferredSpawnData.getUuid());

        if (!player.isDead
            && player.world.provider.getDimension() == deferredSpawnData.getDimensionId()) {
          this.attemptSpawn(player.world, deferredSpawnData);
        }

        this.deferredSpawnDataList.remove(i);
      }
    }
  }

  private void attemptSpawn(World world, DeferredSpawnData deferredSpawnData) {

    if (deferredSpawnData.getSpawnType() == InvasionTemplateWave.EnumSpawnType.ground
        && !world.isSideSolid(deferredSpawnData.getPos().down(), EnumFacing.UP)) {

      InvasionTemplateWave.SecondaryMob secondaryMob = deferredSpawnData.getSecondaryMob();

      MobTemplate mobTemplate = this.mobTemplateFunction.apply(secondaryMob.id);

      if (mobTemplate == null) {
        ModOnslaught.LOG.log(Level.SEVERE, "Unknown mob template id: " + secondaryMob.id);
        return;
      }

      EntityLiving entity = this.mobTemplateEntityFactory.create(mobTemplate, world);

      if (entity == null) {
        ModOnslaught.LOG.log(Level.SEVERE, "Unknown entity id: " + mobTemplate.id);
        return;
      }

      EntityLiving deferredSpawnDataEntity = deferredSpawnData.getEntityLiving();
      entity.setPosition(deferredSpawnDataEntity.posX, deferredSpawnDataEntity.posY, deferredSpawnDataEntity.posZ);

      Predicate<EntityLiving> predicate = this.spawnPredicateFactory.create(this.invasionSpawnDataConverter.convert(secondaryMob.spawn));

      if (!predicate.test(entity)) {
        return;
      }

      // apply player target, chase long distance, and invasion data tags
      this.entityInvasionDataInjector.inject(entity, deferredSpawnData.getUuid(), deferredSpawnData.getWaveIndex(), deferredSpawnData.getMobIndex());

      if (world.spawnEntity(entity)) {
        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
      }

    } else {
      EntityLiving entity = deferredSpawnData.getEntityLiving();

      // Check and clear collisions
      this.checkAndClearCollisions(world, entity);

      // apply player target, chase long distance, and invasion data tags
      this.entityInvasionDataInjector.inject(entity, deferredSpawnData.getUuid(), deferredSpawnData.getWaveIndex(), deferredSpawnData.getMobIndex());

      if (world.spawnEntity(entity)) {
        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
      }
    }
  }

  private void checkAndClearCollisions(World world, EntityLiving entity) {

    List<AxisAlignedBB> collisionBoxes = world.getCollisionBoxes(entity, entity.getEntityBoundingBox());

    if (!collisionBoxes.isEmpty()) {

      for (AxisAlignedBB collisionBox : collisionBoxes) {
        BlockPos blockPos = new BlockPos(collisionBox.getCenter());
        world.setBlockToAir(blockPos);
      }

      world.newExplosion(null, entity.posX, entity.posY, entity.posZ, 7, false, false);
    }
  }
}