package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Responsible for transitioning a player's invasion state from eligible to pending.
 */
public class StateChangeEligibleToPending {

  private final Set<UUID> eligiblePlayers;
  private final InvasionSelector invasionSelector;
  private final InvasionDataFactory invasionDataFactory;

  public StateChangeEligibleToPending(
      Set<UUID> eligiblePlayers,
      InvasionSelector invasionSelector,
      InvasionDataFactory invasionDataFactory
  ) {

    this.eligiblePlayers = eligiblePlayers;
    this.invasionSelector = invasionSelector;
    this.invasionDataFactory = invasionDataFactory;
  }

  public void process(
      Supplier<List<EntityPlayerMP>> playerListSupplier,
      Function<UUID, EntityPlayerMP> playerFromUUIDFunction,
      long totalWorldTime
  ) {

    // Check that we don't exceed the max concurrent invasion value.
    List<EntityPlayerMP> playerList = playerListSupplier.get();
    int concurrentInvasions = this.countInvasions(playerList);

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

        IInvasionPlayerData data = CapabilityInvasion.get(player);
        data.setInvasionState(IInvasionPlayerData.EnumInvasionState.Pending);
        data.setInvasionData(this.invasionDataFactory.create(invasionTemplateId, player.getRNG(), totalWorldTime));

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

  private int countInvasions(List<EntityPlayerMP> players) {

    int result = 0;

    for (EntityPlayerMP player : players) {
      IInvasionPlayerData data = CapabilityInvasion.get(player);
      IInvasionPlayerData.EnumInvasionState invasionState = data.getInvasionState();

      if (invasionState == IInvasionPlayerData.EnumInvasionState.Pending
          || invasionState == IInvasionPlayerData.EnumInvasionState.Active) {
        result += 1;
      }
    }

    return result;
  }
}
