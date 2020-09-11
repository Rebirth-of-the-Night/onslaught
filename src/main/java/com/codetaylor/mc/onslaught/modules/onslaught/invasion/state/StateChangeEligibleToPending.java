package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector.InvasionSelector;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntSupplier;

/**
 * Responsible for transitioning a player's invasion state from eligible to pending.
 */
public class StateChangeEligibleToPending
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final Set<UUID> eligiblePlayers;
  private final InvasionSelector invasionSelector;
  private final InvasionPlayerDataFactory invasionPlayerDataFactory;
  private final IntSupplier maxConcurrentInvasionsSupplier;
  private final InvasionCounter invasionCounter;

  public StateChangeEligibleToPending(
      Set<UUID> eligiblePlayers,
      InvasionSelector invasionSelector,
      InvasionPlayerDataFactory invasionPlayerDataFactory,
      IntSupplier maxConcurrentInvasionsSupplier,
      InvasionCounter invasionCounter
  ) {

    this.eligiblePlayers = eligiblePlayers;
    this.invasionSelector = invasionSelector;
    this.invasionPlayerDataFactory = invasionPlayerDataFactory;
    this.maxConcurrentInvasionsSupplier = maxConcurrentInvasionsSupplier;
    this.invasionCounter = invasionCounter;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    if (worldTime % 24000 > 1000) {
      return;
    }

    // Check that we don't exceed the max concurrent invasion value.
    int concurrentInvasions = this.invasionCounter.count(
        uuid -> invasionGlobalSavedData.getPlayerData(uuid).getInvasionState(),
        playerList.getPlayers()
    );
    int maxConcurrentInvasions = this.maxConcurrentInvasionsSupplier.getAsInt();

    if (concurrentInvasions >= maxConcurrentInvasions) {
      return;
    }

    int allowedInvasions = maxConcurrentInvasions - concurrentInvasions;
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
        data.setInvasionData(this.invasionPlayerDataFactory.create(invasionTemplateId, player.getRNG(), worldTime));
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
}
