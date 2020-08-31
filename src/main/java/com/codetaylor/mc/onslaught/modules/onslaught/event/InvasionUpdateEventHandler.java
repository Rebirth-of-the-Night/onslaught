package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionManager;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Responsible for triggering the invasion manager update when the overworld ticks.
 */
public class InvasionUpdateEventHandler {

  private final InvasionManager invasionManager;

  public InvasionUpdateEventHandler(InvasionManager invasionManager) {

    this.invasionManager = invasionManager;
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

    this.invasionManager.update((WorldServer) event.world);
  }
}
