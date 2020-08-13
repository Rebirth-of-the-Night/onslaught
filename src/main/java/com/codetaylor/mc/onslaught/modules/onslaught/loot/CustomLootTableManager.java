package com.codetaylor.mc.onslaught.modules.onslaught.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class CustomLootTableManager
    extends LootTableManager {

  private final LootTableManager lootTableManager;
  private final boolean initialized;

  public CustomLootTableManager(@Nullable File folder, LootTableManager lootTableManager) {

    super(folder);
    this.lootTableManager = lootTableManager;
    this.initialized = true;
    this.reloadLootTables();
  }

  @Nonnull
  @Override
  public LootTable getLootTableFromLocation(@Nonnull ResourceLocation resourceLocation) {

    LootTable lootTable = super.getLootTableFromLocation(resourceLocation);

    if (lootTable == LootTable.EMPTY_LOOT_TABLE) {
      return this.lootTableManager.getLootTableFromLocation(resourceLocation);
    }

    return lootTable;
  }

  @Override
  public void reloadLootTables() {

    if (this.initialized) {
      super.reloadLootTables();
      this.lootTableManager.reloadLootTables();
    }
  }
}
