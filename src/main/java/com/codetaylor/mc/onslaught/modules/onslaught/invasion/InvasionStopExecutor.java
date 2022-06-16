package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

/** Responsible for executing the logic required to stop invasions. */
public class InvasionStopExecutor {

  private final IntSupplier invasionPlayerTimerValueSupplier;
  private final Predicate<InvasionPlayerData> invasionFinishedPredicate;

  public InvasionStopExecutor(
      IntSupplier invasionPlayerTimerValueSupplier,
      Predicate<InvasionPlayerData> invasionFinishedPredicate) {

    this.invasionPlayerTimerValueSupplier = invasionPlayerTimerValueSupplier;
    this.invasionFinishedPredicate = invasionFinishedPredicate;
  }

  public void stopAllWithCheck(
      List<EntityPlayerMP> players, InvasionGlobalSavedData invasionGlobalSavedData) {

    for (EntityPlayerMP player : players) {
      InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());
      this.stopWithCheck(player, invasionGlobalSavedData, data);
    }
  }

  public boolean stopWithCheck(
      EntityPlayerMP player,
      InvasionGlobalSavedData invasionGlobalSavedData,
      InvasionPlayerData data) {

    if (this.invasionFinishedPredicate.test(data)) {
      // We need to send the event before the invasion data is erased so that
      // the event handlers interested in this event have access to the invasion
      // that is ending.
      MinecraftForge.EVENT_BUS.post(
          new InvasionStateChangedEvent(
              player,
              InvasionPlayerData.EnumInvasionState.Active,
              InvasionPlayerData.EnumInvasionState.Waiting));

      data.setTicksUntilEligible(this.invasionPlayerTimerValueSupplier.getAsInt());
      data.setInvasionState(InvasionPlayerData.EnumInvasionState.Waiting);
      data.setInvasionData(null);

      invasionGlobalSavedData.markDirty();

      if (ModuleOnslaughtConfig.DEBUG.INVASION_STATE) {
        String message =
            String.format("Set invasion state to %s for player %s", "Waiting", player.getName());
        ModOnslaught.LOG.fine(message);
        System.out.println(message);
      }

      return true;
    }

    return false;
  }
}
