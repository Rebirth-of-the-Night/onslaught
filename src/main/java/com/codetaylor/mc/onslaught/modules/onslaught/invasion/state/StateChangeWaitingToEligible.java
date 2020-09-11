package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;

import java.util.Set;
import java.util.UUID;

/**
 * Responsible for transitioning a player's invasion state from waiting to eligible.
 */
public class StateChangeWaitingToEligible
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final Set<UUID> eligiblePlayers;

  public StateChangeWaitingToEligible(Set<UUID> eligiblePlayers) {

    this.eligiblePlayers = eligiblePlayers;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    for (EntityPlayerMP player : playerList.getPlayers()) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

      // If the player isn't waiting and isn't eligible, we want to short-circuit.
      // If the player is eligible, we want to add them to the list and update
      // their state again. The list is an ordered set, so adding them again
      // will not add a duplicate entry, nor will it change their position in
      // the list. We want to add them again because this covers the case in
      // which a player logs out after becoming eligible. If they log back in,
      // they will be added to the list again.

      if (data.getInvasionState() != InvasionPlayerData.EnumInvasionState.Waiting
          || data.getInvasionState() != InvasionPlayerData.EnumInvasionState.Eligible) {
        return;
      }

      int ticksUntilNextInvasion = data.getTicksUntilEligible();

      if (ticksUntilNextInvasion > 0) {
        data.setTicksUntilEligible(ticksUntilNextInvasion - updateIntervalTicks);

      } else {
        this.eligiblePlayers.add(player.getUniqueID());
        data.setInvasionState(InvasionPlayerData.EnumInvasionState.Eligible);
      }

      invasionGlobalSavedData.markDirty();
    }
  }
}