package com.codetaylor.mc.onslaught.modules.onslaught.loot;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.MethodHandleHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableManager;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.util.logging.Level;

/**
 * Responsible for injecting the custom loot table manager.
 */
public class CustomLootTableManagerInjector {

  private static final MethodHandle world$lootTableGetter;
  private static final MethodHandle world$lootTableSetter;

  static {
    /*
    MC 1.12: net/minecraft/world/World.lootTable
    Name: B => field_184151_B => lootTable
    Comment: None
    Side: BOTH
    AT: public net.minecraft.world.World field_184151_B # lootTable
     */
    world$lootTableGetter = MethodHandleHelper.unreflectGetter(World.class, "field_184151_B");
    world$lootTableSetter = MethodHandleHelper.unreflectSetter(World.class, "field_184151_B");
  }

  private final File path;

  public CustomLootTableManagerInjector(File path) {

    this.path = path;
  }

  public void inject(World world) {

    try {
      LootTableManager lootTableManager = (LootTableManager) world$lootTableGetter.invokeExact(world);
      CustomLootTableManager customLootTableManager = new CustomLootTableManager(this.path, lootTableManager);
      world$lootTableSetter.invokeExact(world, (LootTableManager) customLootTableManager);

    } catch (Throwable throwable) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error injecting custom loot manager");
      ModOnslaught.LOG.log(Level.SEVERE, throwable.getMessage(), throwable);
    }
  }
}
