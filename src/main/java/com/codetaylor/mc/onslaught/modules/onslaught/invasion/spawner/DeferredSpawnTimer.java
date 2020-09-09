package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;

import java.util.List;

/**
 * Responsible for decreasing all deferred spawn timers.
 */
public class DeferredSpawnTimer
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final List<DeferredSpawnData> deferredSpawnDataList;

  public DeferredSpawnTimer(List<DeferredSpawnData> deferredSpawnDataList) {

    this.deferredSpawnDataList = deferredSpawnDataList;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, World world) {

    for (DeferredSpawnData deferredSpawnData : this.deferredSpawnDataList) {
      deferredSpawnData.setTicksRemaining(deferredSpawnData.getTicksRemaining() - updateIntervalTicks);
    }
  }
}
