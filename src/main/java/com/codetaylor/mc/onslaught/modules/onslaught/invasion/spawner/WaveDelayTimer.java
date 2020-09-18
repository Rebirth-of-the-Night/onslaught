package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;

import java.util.List;
import java.util.function.Predicate;

/**
 * Responsible for updating the wave delay timers for all active invasions.
 */
public class WaveDelayTimer
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final Predicate<InvasionPlayerData.InvasionData> activeWavePredicate;

  public WaveDelayTimer(Predicate<InvasionPlayerData.InvasionData> activeWavePredicate) {

    this.activeWavePredicate = activeWavePredicate;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

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

      // Force a wave to 0 delay if it is the first wave found with remaining
      // delay and the invasion has no active waves. Else, decrement all
      // remaining wave delay timers.
      
      boolean hasActiveWave = this.activeWavePredicate.test(invasionData);
      boolean forcedWaveStart = false;

      for (InvasionPlayerData.InvasionData.WaveData waveData : waveDataList) {
        int delayTicks = waveData.getDelayTicks();

        if (delayTicks > 0) {

          if (!forcedWaveStart && !hasActiveWave) {
            forcedWaveStart = true;
            waveData.setDelayTicks(0);

          } else {
            waveData.setDelayTicks(delayTicks - updateIntervalTicks);
          }

          invasionGlobalSavedData.markDirty();
        }
      }
    }
  }
}