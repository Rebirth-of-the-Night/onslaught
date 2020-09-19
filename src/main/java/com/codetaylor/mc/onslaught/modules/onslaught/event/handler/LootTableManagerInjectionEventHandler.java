package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.loot.CustomLootTableManagerInjector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Responsible for injecting a custom loot table manager.
 */
public class LootTableManagerInjectionEventHandler {

  private final CustomLootTableManagerInjector injector;

  public LootTableManagerInjectionEventHandler(CustomLootTableManagerInjector injector) {

    this.injector = injector;
  }

  @SubscribeEvent
  public void on(WorldEvent.Load event) {

    World world = event.getWorld();

    if (world instanceof WorldServer) {
      this.injector.inject(world);
    }
  }
}
