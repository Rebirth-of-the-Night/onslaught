package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionEntityKilledEvent;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionClientHUDUpdateSender;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.TickCounter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * Responsible for triggering HUD updates to the clients.
 */
public class InvasionClientUpdateEventHandler {

  private static final int INTERVAL_TICKS = 20 * 10;

  private final InvasionClientHUDUpdateSender invasionClientHUDUpdateSender;
  private final TickCounter tickCounter;

  public InvasionClientUpdateEventHandler(InvasionClientHUDUpdateSender invasionClientHUDUpdateSender) {

    this.invasionClientHUDUpdateSender = invasionClientHUDUpdateSender;
    this.tickCounter = new TickCounter(INTERVAL_TICKS);
  }

  private void forceRun() {

    this.tickCounter.setCounter(INTERVAL_TICKS);
  }

  @SubscribeEvent
  public void on(InvasionStateChangedEvent event) {

    // Force run when an invasion is completed or started.

    if (event.getPreviousState() == InvasionPlayerData.EnumInvasionState.Active
        && event.getCurrentState() == InvasionPlayerData.EnumInvasionState.Waiting) {
      // completed
      this.forceRun();

    } else if (event.getPreviousState() == InvasionPlayerData.EnumInvasionState.Waiting
        && event.getCurrentState() == InvasionPlayerData.EnumInvasionState.Active) {
      // started
      this.forceRun();
    }
  }

  @SubscribeEvent
  public void on(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {

    // Force run when a player leaves the server.

    this.forceRun();
  }

  @SubscribeEvent
  public void on(EntityJoinWorldEvent event) {

    // Force run when an entity joins the world.

    this.forceRun();
  }

  @SubscribeEvent
  public void on(InvasionEntityKilledEvent event) {

    // Force run when an invasion entity is killed.

    this.forceRun();
  }

  @SubscribeEvent
  public void on(TickEvent.ServerTickEvent event) {

    if (event.phase != TickEvent.Phase.START) {
      return;
    }

    if (event.side != Side.SERVER) {
      return;
    }

    if (this.tickCounter.increment(1)) {
      MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
      PlayerList playerList = minecraftServer.getPlayerList();
      List<EntityPlayerMP> players = playerList.getPlayers();

      this.invasionClientHUDUpdateSender.update(players);
    }
  }
}