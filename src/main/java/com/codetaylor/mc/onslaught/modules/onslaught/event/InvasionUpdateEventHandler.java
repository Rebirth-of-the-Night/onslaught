package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.TickCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.TickIntervalCounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Responsible for hooking the overworld tick and calling the update method on
 * all registered {@link IInvasionUpdateComponent}s.
 */
public class InvasionUpdateEventHandler {

  /**
   * Responsible for wrapping an {@link IInvasionUpdateComponent} and providing
   * updates only at the given tick interval.
   */
  public static class InvasionTimedUpdateComponent
      implements IInvasionUpdateComponent {

    private final int updateIntervalTicks;

    private final IInvasionUpdateComponent invasionUpdateComponent;
    private final TickCounter tickCounter;

    private final TickIntervalCounter tickIntervalCounter;

    public InvasionTimedUpdateComponent(int updateIntervalTicks, IInvasionUpdateComponent invasionUpdateComponent) {

      this.updateIntervalTicks = updateIntervalTicks;
      this.invasionUpdateComponent = invasionUpdateComponent;
      this.tickCounter = new TickCounter(this.updateIntervalTicks);
      this.tickIntervalCounter = new TickIntervalCounter();
    }

    @Override
    public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

      if (this.tickCounter.increment(updateIntervalTicks)) {
        int interval = this.tickIntervalCounter.updateAndGet(this.updateIntervalTicks, worldTime);
        this.invasionUpdateComponent.update(interval, invasionGlobalSavedData, playerList, worldTime);
      }
    }
  }

  /**
   * An invasion component, updated on the Overworld's server tick.
   */
  public interface IInvasionUpdateComponent {

    void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime);

  }

  /**
   * The {@link IInvasionUpdateComponent}s to update.
   */
  private final IInvasionUpdateComponent[] components;

  private final TickIntervalCounter tickIntervalCounter;

  public InvasionUpdateEventHandler(IInvasionUpdateComponent[] components) {

    this.components = components;
    this.tickIntervalCounter = new TickIntervalCounter();
  }

  @SubscribeEvent
  public void on(TickEvent.WorldTickEvent event) {

    World world = event.world;

    if (world.isRemote) {
      return;
    }

    if (event.phase != TickEvent.Phase.END) {
      return;
    }

    if (world.provider.getDimension() != 0) {
      return;
    }

    if (!(world instanceof WorldServer)) {
      return;
    }

    MinecraftServer minecraftServer = world.getMinecraftServer();

    // We check above if this is instance of WorldServer, shouldn't be null.
    if (minecraftServer == null) {
      return;
    }

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(world);
    PlayerList playerList = minecraftServer.getPlayerList();
    long worldTime = world.getWorldTime();
    int updateIntervalTicks = this.tickIntervalCounter.updateAndGet(1, worldTime);

    for (IInvasionUpdateComponent component : this.components) {
      component.update(updateIntervalTicks, invasionGlobalSavedData, playerList, worldTime);
    }
  }
}