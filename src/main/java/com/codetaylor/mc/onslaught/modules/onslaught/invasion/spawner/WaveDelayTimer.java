package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;

import java.util.List;

/**
 * Responsible for updating the wave delay timers for all active invasions.
 */
public class WaveDelayTimer
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, World world) {

    for (EntityPlayerMP player : playerList.getPlayers()) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

      if (data.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      if (invasionData == null) {
        continue;
      }

      List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.getWaveDataList();

      for (InvasionPlayerData.InvasionData.WaveData waveData : waveDataList) {
        int delayTicks = waveData.getDelayTicks();

        if (delayTicks > 0) {
          waveData.setDelayTicks(delayTicks - updateIntervalTicks);
          invasionGlobalSavedData.markDirty();
        }
      }
    }
  }
}
