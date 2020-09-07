package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Set;
import java.util.UUID;

/**
 * Responsible for transitioning a player's invasion state from waiting to eligible.
 */
public class StateChangeWaitingToEligible {

  private final Set<UUID> eligiblePlayers;

  public StateChangeWaitingToEligible(Set<UUID> eligiblePlayers) {

    this.eligiblePlayers = eligiblePlayers;
  }

  public void process(EntityPlayer entityPlayer, int updateInvervalTicks) {

    IInvasionPlayerData data = CapabilityInvasion.get(entityPlayer);

    // If the player isn't waiting and isn't eligible, we want to short-circuit.
    // If the player is eligible, we want to add them to the list and update
    // their state again. The list is an ordered set, so adding them again
    // will not add a duplicate entry, nor will it change their position in
    // the list. We want to add them again because this covers the case in
    // which a player logs out after becoming eligible. If they log back in,
    // they will be added to the list again.

    if (data.getInvasionState() != IInvasionPlayerData.EnumInvasionState.Waiting
        || data.getInvasionState() != IInvasionPlayerData.EnumInvasionState.Eligible) {
      return;
    }

    int ticksUntilNextInvasion = data.getTicksUntilEligible();

    if (ticksUntilNextInvasion > 0) {
      data.setTicksUntilEligible(ticksUntilNextInvasion - updateInvervalTicks);

    } else {
      this.eligiblePlayers.add(entityPlayer.getUniqueID());
      data.setInvasionState(IInvasionPlayerData.EnumInvasionState.Eligible);
    }
  }
}
