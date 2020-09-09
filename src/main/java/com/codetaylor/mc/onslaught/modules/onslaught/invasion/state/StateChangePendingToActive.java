package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

/**
 * Responsible for executing an invasion state transition from pending to active.
 */
public class StateChangePendingToActive {

  public void process(InvasionGlobalSavedData invasionGlobalSavedData, long totalWorldTime, List<EntityPlayerMP> playerList) {

    for (EntityPlayerMP player : playerList) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

      if (data.getInvasionState() != InvasionPlayerData.EnumInvasionState.Pending) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      if (invasionData == null) {
        continue;
      }

      if (invasionData.getTimestamp() <= totalWorldTime) {
        data.setInvasionState(InvasionPlayerData.EnumInvasionState.Active);
        invasionGlobalSavedData.markDirty();
      }
    }
  }
}
