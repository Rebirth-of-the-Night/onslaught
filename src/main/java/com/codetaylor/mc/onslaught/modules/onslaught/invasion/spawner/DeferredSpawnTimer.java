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

  private final List<DeferredSpawn> deferredSpawnList;

  public DeferredSpawnTimer(List<DeferredSpawn> deferredSpawnList) {

    this.deferredSpawnList = deferredSpawnList;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, World world) {

    for (DeferredSpawn deferredSpawn : this.deferredSpawnList) {
      deferredSpawn.setTicksRemaining(deferredSpawn.getTicksRemaining() - updateIntervalTicks);
    }
  }
}
