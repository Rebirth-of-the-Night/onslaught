package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.athenaeum.util.TickCounter;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
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

  public InvasionUpdateEventHandler(IInvasionUpdateComponent[] components) {

    this.components = components;
  }

  public static class InvasionTimedUpdateComponent
      implements IInvasionUpdateComponent {

    private final int updateIntervalTicks;
    private final IInvasionUpdateComponent invasionUpdateComponent;
    private final TickCounter tickCounter;

    public InvasionTimedUpdateComponent(int updateIntervalTicks, IInvasionUpdateComponent invasionUpdateComponent) {

      this.updateIntervalTicks = updateIntervalTicks;
      this.invasionUpdateComponent = invasionUpdateComponent;
      this.tickCounter = new TickCounter(this.updateIntervalTicks);
    }

    @Override
    public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, World world) {

      if (this.tickCounter.increment(updateIntervalTicks)) {
        this.invasionUpdateComponent.update(this.updateIntervalTicks, invasionGlobalSavedData, playerList, world);
      }
    }
  }

  private final IInvasionUpdateComponent[] components;

  public interface IInvasionUpdateComponent {

    void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, World world);
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

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(event.world);
    PlayerList playerList = minecraftServer.getPlayerList();

    for (IInvasionUpdateComponent component : this.components) {
      component.update(1, invasionGlobalSavedData, playerList, event.world);
    }
  }
}
