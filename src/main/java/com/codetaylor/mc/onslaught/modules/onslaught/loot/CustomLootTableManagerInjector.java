package com.codetaylor.mc.onslaught.modules.onslaught.loot;

import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableManager;

import java.io.File;

/** Responsible for injecting the custom loot table manager. */
public class CustomLootTableManagerInjector {

  private final File path;

  public CustomLootTableManagerInjector(File path) {
    this.path = path;
  }

  public void inject(World world) {
      LootTableManager lootTableManager = world.getLootTableManager();
      world.lootTable = new CustomLootTableManager(this.path, lootTableManager);
  }
}
