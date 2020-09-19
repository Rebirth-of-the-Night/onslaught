package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.EntityInvasionPeriodicWorldCleanup;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.List;

/**
 * Responsible for triggering the invasion entity cleanup in response to
 * specific events.
 */
public class InvasionCleanupEventHandler {

  private static final int INTERVAL_TICKS = 20 * 10;

  private final EntityInvasionPeriodicWorldCleanup entityInvasionPeriodicWorldCleanup;
  private final Int2LongMap lastRunMap;

  public InvasionCleanupEventHandler(EntityInvasionPeriodicWorldCleanup entityInvasionPeriodicWorldCleanup) {

    this.entityInvasionPeriodicWorldCleanup = entityInvasionPeriodicWorldCleanup;
    this.lastRunMap = new Int2LongOpenHashMap();
    this.lastRunMap.defaultReturnValue(0);
  }

  private void forceRun() {

    this.lastRunMap.clear();
  }

  @SubscribeEvent
  public void on(InvasionStateChangedEvent event) {

    // Force run when an invasion is completed.

    if (event.getPreviousState() == InvasionPlayerData.EnumInvasionState.Active
        && event.getCurrentState() == InvasionPlayerData.EnumInvasionState.Waiting) {
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
  public void on(TickEvent.WorldTickEvent event) {

    World world = event.world;

    if (world.isRemote || !(world instanceof WorldServer)) {
      return;
    }

    MinecraftServer minecraftServer = world.getMinecraftServer();

    if (minecraftServer == null) {
      return;
    }

    int dimensionId = world.provider.getDimension();

    // world time needs to be from overworld to remain consistent
    // some modded worlds may not increment time
    WorldServer overworld = minecraftServer.getWorld(0);
    long worldTime = overworld.getWorldTime();

    if (worldTime - this.lastRunMap.get(dimensionId) >= INTERVAL_TICKS) {
      this.lastRunMap.put(dimensionId, worldTime);

      List<EntityLiving> entityLivingList = world.getEntities(
          EntityLiving.class,
          entity -> entity != null && entity.getEntityData().hasKey(Tag.ONSLAUGHT)
      );

      PlayerList playerList = minecraftServer.getPlayerList();

      this.entityInvasionPeriodicWorldCleanup.cleanup(worldTime, entityLivingList, playerList::getPlayerByUUID);
    }
  }
}