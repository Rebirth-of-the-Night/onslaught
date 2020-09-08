package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.athenaeum.util.TickCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.StateChangeActiveToWaiting;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.StateChangeEligibleToPending;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.StateChangePendingToActive;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.StateChangeWaitingToEligible;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

/**
 * Contains all invasion state change event handlers. These are responsible
 * for determining if the state change processes should run, when they should
 * run, and for supplying the processes with their required parameters.
 */
public final class InvasionStateChangeEventHandlers {

  /**
   * Responsible for timing an invasion state transition from waiting to eligible.
   */
  public static class WaitingToEligible {

    private static final int UPDATE_INTERVAL_TICKS = 20;

    private final StateChangeWaitingToEligible stateChangeWaitingToEligible;
    private final TickCounter tickCounter;

    public WaitingToEligible(StateChangeWaitingToEligible stateChangeWaitingToEligible) {

      this.stateChangeWaitingToEligible = stateChangeWaitingToEligible;
      this.tickCounter = new TickCounter(UPDATE_INTERVAL_TICKS);
    }

    @SubscribeEvent
    public void on(TickEvent.PlayerTickEvent event) {

      if (event.phase != TickEvent.Phase.END) {
        return;
      }

      if (!this.tickCounter.increment()) {
        return;
      }

      EntityPlayer entityPlayer = event.player;

      if (entityPlayer.world.isRemote) {
        return;
      }

      if (!(entityPlayer instanceof EntityPlayerMP)) {
        return;
      }

      this.stateChangeWaitingToEligible.process(entityPlayer, UPDATE_INTERVAL_TICKS);
    }
  }

  /**
   * Responsible for timing an invasion state change from eligible to pending.
   * Operates within a 1000 tick window from 0 to 1000 with respect to the Overworld.
   */
  public static class EligibleToPending {

    private final StateChangeEligibleToPending stateChangeEligibleToPending;

    public EligibleToPending(StateChangeEligibleToPending stateChangeEligibleToPending) {

      this.stateChangeEligibleToPending = stateChangeEligibleToPending;
    }

    @SubscribeEvent
    public void on(TickEvent.WorldTickEvent event) {

      if (event.world.isRemote) {
        return;
      }

      if (event.phase != TickEvent.Phase.END) {
        return;
      }

      if (event.world.provider.getDimension() != 0) {
        return;
      }

      if (!(event.world instanceof WorldServer)) {
        return;
      }

      if (event.world.getWorldTime() >= 1000) {
        return;
      }

      MinecraftServer minecraftServer = event.world.getMinecraftServer();

      // We check above if this is instance of WorldServer, shouldn't be null.
      if (minecraftServer == null) {
        return;
      }

      PlayerList playerList = minecraftServer.getPlayerList();
      long totalWorldTime = event.world.getTotalWorldTime();
      this.stateChangeEligibleToPending.process(playerList::getPlayers, playerList::getPlayerByUUID, totalWorldTime);
    }
  }

  /**
   * Responsible for timing an invasion state transition from pending to active.
   */
  public static class PendingToActive {

    private final StateChangePendingToActive stateChangePendingToActive;

    public PendingToActive(StateChangePendingToActive stateChangePendingToActive) {

      this.stateChangePendingToActive = stateChangePendingToActive;
    }

    @SubscribeEvent
    public void on(TickEvent.WorldTickEvent event) {

      if (event.world.isRemote) {
        return;
      }

      if (event.phase != TickEvent.Phase.END) {
        return;
      }

      if (event.world.provider.getDimension() != 0) {
        return;
      }

      if (!(event.world instanceof WorldServer)) {
        return;
      }

      MinecraftServer minecraftServer = event.world.getMinecraftServer();

      // We check above if this is instance of WorldServer, shouldn't be null.
      if (minecraftServer == null) {
        return;
      }

      long totalWorldTime = event.world.getTotalWorldTime();
      PlayerList playerList = minecraftServer.getPlayerList();
      List<EntityPlayerMP> players = playerList.getPlayers();
      this.stateChangePendingToActive.process(totalWorldTime, players);
    }
  }

  /**
   * Responsible for timing an invasion state transition from active to waiting.
   */
  public static class ActiveToWaiting {

    private static final int UPDATE_INTERVAL_TICKS = 20;

    private final StateChangeActiveToWaiting stateChangeActiveToWaiting;
    private final TickCounter tickCounter;

    public ActiveToWaiting(StateChangeActiveToWaiting stateChangeActiveToWaiting) {

      this.stateChangeActiveToWaiting = stateChangeActiveToWaiting;
      this.tickCounter = new TickCounter(UPDATE_INTERVAL_TICKS);
    }

    @SubscribeEvent
    public void on(TickEvent.PlayerTickEvent event) {

      if (event.phase != TickEvent.Phase.END) {
        return;
      }

      if (!this.tickCounter.increment()) {
        return;
      }

      EntityPlayer entityPlayer = event.player;

      if (entityPlayer.world.isRemote) {
        return;
      }

      if (!(entityPlayer instanceof EntityPlayerMP)) {
        return;
      }

      this.stateChangeActiveToWaiting.process(entityPlayer);
    }
  }

  private InvasionStateChangeEventHandlers() {
    //
  }
}