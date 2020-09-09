package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Responsible for spawning a deferred mob when its delay timer is expired.
 */
public class DeferredSpawner
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final EntityInvasionDataInjector entityInvasionDataInjector;
  private final List<DeferredSpawnData> deferredSpawnDataList;

  public DeferredSpawner(EntityInvasionDataInjector entityInvasionDataInjector, List<DeferredSpawnData> deferredSpawnDataList) {

    this.entityInvasionDataInjector = entityInvasionDataInjector;
    this.deferredSpawnDataList = deferredSpawnDataList;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, World world) {

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
      // TODO spawn secondary mob

    } else {
      EntityLiving entity = deferredSpawnData.getEntityLiving();

      // Check and clear collisions
      List<AxisAlignedBB> collisionBoxes = world.getCollisionBoxes(entity, entity.getEntityBoundingBox());

      if (!collisionBoxes.isEmpty()) {

        for (AxisAlignedBB collisionBox : collisionBoxes) {
          BlockPos blockPos = new BlockPos(collisionBox.getCenter());
          world.setBlockToAir(blockPos);
        }

        world.newExplosion(null, entity.posX, entity.posY, entity.posZ, 7, false, false);
      }

      // apply player target, chase long distance, and invasion data tags
      this.entityInvasionDataInjector.inject(entity, deferredSpawnData.getUuid(), deferredSpawnData.getWaveIndex(), deferredSpawnData.getMobIndex());

      if (world.spawnEntity(entity)) {
        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
      }
    }
  }
}