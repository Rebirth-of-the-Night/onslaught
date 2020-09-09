package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector.InvasionSelector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Responsible for transitioning a player's invasion state from eligible to pending.
 */
public class StateChangeEligibleToPending
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

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

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, World world) {

    if (world.getWorldTime() > 1000) {
      return;
    }

    // Check that we don't exceed the max concurrent invasion value.
    int concurrentInvasions = this.countInvasions(invasionGlobalSavedData, playerList.getPlayers());

    if (concurrentInvasions >= ModuleOnslaughtConfig.INVASION.MAX_CONCURRENT_INVASIONS) {
      return;
    }

    int allowedInvasions = ModuleOnslaughtConfig.INVASION.MAX_CONCURRENT_INVASIONS - concurrentInvasions;
    List<UUID> toRemove = new ArrayList<>(this.eligiblePlayers.size());

    for (UUID uuid : this.eligiblePlayers) {
      EntityPlayerMP player = playerList.getPlayerByUUID(uuid);

      // This will be null if the player isn't online by the time this executes.
      // If the player isn't online, we need to remove them from the eligible
      // players list.
      //noinspection ConstantConditions
      if (player != null) {
        String invasionTemplateId = this.invasionSelector.selectInvasionForPlayer(player);

        if (invasionTemplateId == null) {
          continue;
        }

        InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(uuid);
        data.setInvasionState(InvasionPlayerData.EnumInvasionState.Pending);
        data.setInvasionData(this.invasionPlayerDataFactory.create(invasionTemplateId, player.getRNG(), world.getTotalWorldTime(), world.getWorldTime()));
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
