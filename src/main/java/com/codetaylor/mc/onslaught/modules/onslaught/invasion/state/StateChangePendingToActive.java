package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.MinecraftForge;

/**
 * Responsible for executing an invasion state transition from pending to active.
 */
public class StateChangePendingToActive
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    for (EntityPlayerMP player : playerList.getPlayers()) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

      if (data.getInvasionState() != InvasionPlayerData.EnumInvasionState.Pending) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

      if (invasionData == null) {
        continue;
      }

      if (invasionData.getTimestamp() <= worldTime) {
        data.setInvasionState(InvasionPlayerData.EnumInvasionState.Active);
        invasionGlobalSavedData.markDirty();

        if (ModuleOnslaughtConfig.DEBUG.INVASION_STATE) {
          String message = String.format("Set invasion state to %s for player %s", "Active", player.getName());
          ModOnslaught.LOG.fine(message);
          System.out.println(message);
        }

        MinecraftForge.EVENT_BUS.post(new InvasionStateChangedEvent(player, InvasionPlayerData.EnumInvasionState.Pending, InvasionPlayerData.EnumInvasionState.Active));
      }
    }
  }
}