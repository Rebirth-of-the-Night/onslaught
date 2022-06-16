package com.codetaylor.mc.onslaught.modules.onslaught.loot;

import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;

/**
 * Responsible for checking a custom folder for loot tables and deferring to the default MC {@link
 * LootTableManager} if no loot table is found.
 */
public class CustomLootTableManager extends LootTableManager {

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
