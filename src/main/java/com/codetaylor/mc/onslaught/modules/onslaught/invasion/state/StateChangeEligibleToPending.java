package com.codetaylor.mc.onslaught.modules.onslaught.invasion.state;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerDataFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionTimestampFunction;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionWarningMessageTimestampFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.logging.Level;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionMaxDurationFunction;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.common.MinecraftForge;

/** Responsible for transitioning a player's invasion state from eligible to pending. */
public class StateChangeEligibleToPending
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final Set<UUID> eligiblePlayers;
  private final Function<EntityPlayerMP, String> invasionSelectorFunction;
  private final InvasionPlayerDataFactory invasionPlayerDataFactory;
  private final IntSupplier maxConcurrentInvasionsSupplier;
  private final InvasionCounter invasionCounter;
  private final InvasionTimestampFunction invasionTimestampFunction;
  private final InvasionMaxDurationFunction maxInvasionDurationFunction;
  private final InvasionWarningMessageTimestampFunction invasionWarningMessageTimestampFunction;

  public StateChangeEligibleToPending(
      Set<UUID> eligiblePlayers,
      Function<EntityPlayerMP, String> invasionSelectorFunction,
      InvasionPlayerDataFactory invasionPlayerDataFactory,
      IntSupplier maxConcurrentInvasionsSupplier,
      InvasionCounter invasionCounter,
      InvasionTimestampFunction invasionTimestampFunction,
      InvasionMaxDurationFunction maxInvasionDurationFunction,
      InvasionWarningMessageTimestampFunction invasionWarningMessageTimestampFunction) {

    this.eligiblePlayers = eligiblePlayers;
    this.invasionSelectorFunction = invasionSelectorFunction;
    this.invasionPlayerDataFactory = invasionPlayerDataFactory;
    this.maxConcurrentInvasionsSupplier = maxConcurrentInvasionsSupplier;
    this.invasionCounter = invasionCounter;
    this.invasionTimestampFunction = invasionTimestampFunction;
    this.maxInvasionDurationFunction = maxInvasionDurationFunction;
    this.invasionWarningMessageTimestampFunction = invasionWarningMessageTimestampFunction;
  }

  @Override
  public void update(
      int updateIntervalTicks,
      InvasionGlobalSavedData invasionGlobalSavedData,
      PlayerList playerList,
      long worldTime) {

    if (worldTime % 24000 > 1000) {
      return;
    }

    // Check that we don't exceed the max concurrent invasion value.
    int concurrentInvasions =
        this.invasionCounter.count(
            uuid -> invasionGlobalSavedData.getPlayerData(uuid).getInvasionState(),
            playerList.getPlayers());
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
        String invasionTemplateId = this.invasionSelectorFunction.apply(player);

        if (invasionTemplateId == null) {
          ModOnslaught.LOG.log(
              Level.SEVERE, "Unable to select invasion for player: " + player.getName());
          continue;
        }

        long invasionTimestamp = this.invasionTimestampFunction.apply(worldTime);
        long maxInvasionDuration = this.maxInvasionDurationFunction.apply(worldTime);
        long invasionWarningMessageTimestamp =
            this.invasionWarningMessageTimestampFunction.apply(
                invasionTemplateId, invasionTimestamp);

        InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(uuid);
        InvasionPlayerData.InvasionData invasionData =
            this.invasionPlayerDataFactory.create(
                invasionTemplateId,
                UUID.randomUUID(),
                player.getRNG(),
                invasionTimestamp,
                maxInvasionDuration,
                invasionWarningMessageTimestamp);
        data.setInvasionState(InvasionPlayerData.EnumInvasionState.Pending);
        data.setInvasionData(invasionData);
        invasionGlobalSavedData.markDirty();

        allowedInvasions -= 1;

        if (ModuleOnslaughtConfig.DEBUG.INVASION_STATE) {
          String message =
              String.format("Set invasion state to %s for player %s", "Pending", player.getName());
          ModOnslaught.LOG.fine(message);
          System.out.println(message);

          if (invasionData != null) {
            ModOnslaught.LOG.fine(invasionData.toString());
            System.out.println(invasionData.toString());
          }
        }

        MinecraftForge.EVENT_BUS.post(
            new InvasionStateChangedEvent(
                player,
                InvasionPlayerData.EnumInvasionState.Eligible,
                InvasionPlayerData.EnumInvasionState.Pending));
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
