package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketDeferredSpawn;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Responsible for sending deferred spawn particle packets to the client.
 */
public class DeferredSpawnClientParticlePacketSender
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final List<DeferredSpawnData> deferredSpawnDataList;

  public DeferredSpawnClientParticlePacketSender(List<DeferredSpawnData> deferredSpawnDataList) {

    this.deferredSpawnDataList = deferredSpawnDataList;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    for (DeferredSpawnData deferredSpawnData : this.deferredSpawnDataList) {

      int dimensionId = deferredSpawnData.getDimensionId();
      EntityLiving entityLiving = deferredSpawnData.getEntityLiving();
      BlockPos blockPos = new BlockPos(entityLiving);
      SCPacketDeferredSpawn packet = new SCPacketDeferredSpawn(entityLiving.posX, entityLiving.posY, entityLiving.posZ);
      ModuleOnslaught.PACKET_SERVICE.sendToAllAround(packet, dimensionId, blockPos);
    }
  }
}
