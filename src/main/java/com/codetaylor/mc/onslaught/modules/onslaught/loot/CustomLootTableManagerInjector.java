package com.codetaylor.mc.onslaught.modules.onslaught.loot;

import com.codetaylor.mc.onslaught.ModOnslaught;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;

/**
 * Responsible for injecting the custom loot table manager.
 */
public class CustomLootTableManagerInjector {

  private static final MethodHandle world$lootTableGetter;

  static {
    MethodHandle methodHandle;

    try {
      methodHandle = MethodHandles.lookup().unreflectGetter(
          /*
          MC 1.12: net/minecraft/world/World.lootTable
          Name: B => field_184151_B => lootTable
          Comment: None
          Side: BOTH
          AT: public net.minecraft.world.World field_184151_B # lootTable
           */
          ObfuscationReflectionHelper.findField(World.class, "field_184151_B")
      );

    } catch (IllegalAccessException e) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error unreflecting getter for field_184151_B");
      methodHandle = null;
    }

    world$lootTableGetter = methodHandle;
  }

  private static final MethodHandle world$lootTableSetter;

  static {
    MethodHandle methodHandle;

    try {
      methodHandle = MethodHandles.lookup().unreflectSetter(
          /*
          MC 1.12: net/minecraft/world/World.lootTable
          Name: B => field_184151_B => lootTable
          Comment: None
          Side: BOTH
          AT: public net.minecraft.world.World field_184151_B # lootTable
           */
          ObfuscationReflectionHelper.findField(World.class, "field_184151_B")
      );

    } catch (IllegalAccessException e) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error unreflecting setter for field_184151_B");
      methodHandle = null;
    }

    world$lootTableSetter = methodHandle;
  }

  private final File path;

  public CustomLootTableManagerInjector(File path) {

    this.path = path;
  }

  public void inject(World world) {

    if (world$lootTableGetter == null || world$lootTableSetter == null) {
      ModOnslaught.LOG.log(Level.SEVERE, "Unable to inject custom loot table manager due to null method handles");
      return;
    }

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
