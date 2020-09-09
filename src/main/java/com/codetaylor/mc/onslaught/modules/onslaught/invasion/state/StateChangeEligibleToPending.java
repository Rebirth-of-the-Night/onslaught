package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector.InvasionSelector;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * Responsible for transitioning a player's invasion state from eligible to pending.
 */
public class StateChangeEligibleToPending {

  private final Set<UUID> eligiblePlayers;
  private final InvasionSelector invasionSelector;
  private final InvasionPlayerDataFactory invasionPlayerDataFactory;

  public StateChangeEligibleToPending(
      Set<UUID> eligiblePlayers,
      InvasionSelector invasionSelector,
      InvasionPlayerDataFactory invasionPlayerDataFactory
  ) {

    this.eligiblePlayers = eligiblePlayers;
    this.invasionSelector = invasionSelector;
    this.invasionPlayerDataFactory = invasionPlayerDataFactory;
  }

  public void process(
      InvasionGlobalSavedData invasionGlobalSavedData,
      List<EntityPlayerMP> playerList,
      Function<UUID, EntityPlayerMP> playerFromUUIDFunction,
      long totalWorldTime,
      long worldTime
  ) {

    // Check that we don't exceed the max concurrent invasion value.
    int concurrentInvasions = this.countInvasions(invasionGlobalSavedData, playerList);

    if (concurrentInvasions >= ModuleOnslaughtConfig.INVASION.MAX_CONCURRENT_INVASIONS) {
      return;
    }

    int allowedInvasions = ModuleOnslaughtConfig.INVASION.MAX_CONCURRENT_INVASIONS - concurrentInvasions;
    List<UUID> toRemove = new ArrayList<>(this.eligiblePlayers.size());

    for (UUID uuid : this.eligiblePlayers) {
      EntityPlayerMP player = playerFromUUIDFunction.apply(uuid);

      // This will be null if the player isn't online by the time this executes.
      // If the player isn't online, we need to remove them from the eligible
      // players list.
      if (player != null) {
        String invasionTemplateId = this.invasionSelector.selectInvasionForPlayer(player);

        if (invasionTemplateId == null) {
          continue;
        }

        InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(uuid);
        data.setInvasionState(InvasionPlayerData.EnumInvasionState.Pending);
        data.setInvasionData(this.invasionPlayerDataFactory.create(invasionTemplateId, player.getRNG(), totalWorldTime, worldTime));
        invasionGlobalSavedData.markDirty();

        allowedInvasions -= 1;
      }

      toRemove.add(uuid);

      if (allowedInvasions == 0) {
        break;
      }
    }

    for (UUID uuid : toRemove) {
      this.eligiblePlayers.remove(uuid);
    }
  }

  private int countInvasions(InvasionGlobalSavedData invasionGlobalSavedData, List<EntityPlayerMP> players) {

    int result = 0;

    for (EntityPlayerMP player : players) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());
      InvasionPlayerData.EnumInvasionState invasionState = data.getInvasionState();

      if (invasionState == InvasionPlayerData.EnumInvasionState.Pending
          || invasionState == InvasionPlayerData.EnumInvasionState.Active) {
        result += 1;
      }
    }

    return result;
  }
}
