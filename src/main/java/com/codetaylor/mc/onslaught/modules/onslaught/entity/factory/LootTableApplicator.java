package com.codetaylor.mc.onslaught.modules.onslaught.entity.factory;

import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

/** Responsible for applying additional loot tables to an {@link Entity}. */
public class LootTableApplicator {

  public void apply(String[] lootTableIds, Entity entity) {

    for (String lootTableId : lootTableIds) {
      this.apply(lootTableId, entity);
    }
  }

  private void apply(String lootTableId, Entity entity) {

    NBTTagCompound entityData = entity.getEntityData();

    NBTTagCompound modTag;

    if (!entityData.hasKey(Tag.ONSLAUGHT)) {
      modTag = new NBTTagCompound();
      entityData.setTag(Tag.ONSLAUGHT, modTag);

    } else {
      modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);
    }

    NBTTagList extraLootTableList;

    if (!modTag.hasKey(Tag.EXTRA_LOOT_TABLES)) {
      extraLootTableList = new NBTTagList();
      modTag.setTag(Tag.EXTRA_LOOT_TABLES, extraLootTableList);

    } else {
      extraLootTableList = modTag.getTagList(Tag.EXTRA_LOOT_TABLES, Constants.NBT.TAG_STRING);
    }

    extraLootTableList.appendTag(new NBTTagString(lootTableId));
  }
}
