package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

/**
 * Responsible for executing an invasion state transition from pending to active.
 */
public class StateChangePendingToActive {

  public void process(long totalWorldTime, List<EntityPlayerMP> players) {

    for (EntityPlayerMP player : players) {
      IInvasionPlayerData data = CapabilityInvasion.get(player);

      if (data.getInvasionState() != IInvasionPlayerData.EnumInvasionState.Pending) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      if (invasionData == null) {
        continue;
      }

      if (invasionData.timestamp <= totalWorldTime) {
        data.setInvasionState(IInvasionPlayerData.EnumInvasionState.Active);
      }
    }
  }
}
