package com.codetaylor.mc.onslaught.modules.onslaught.loot;

import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;

import java.util.List;

/**
 * Responsible for adding loot from the extra tables to the given loot list.
 */
public class ExtraLootInjector {

	public void inject(Entity entity, DamageSource source, boolean recentlyHit, List<EntityItem> drops) {

		NBTTagCompound entityData = entity.getEntityData();

		if (!entityData.hasKey(Tag.ONSLAUGHT)) {
			return;
		}

		NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

		if (!modTag.hasKey(Tag.EXTRA_LOOT_TABLES)) {
			return;
		}

		NBTTagList tagList = modTag.getTagList(Tag.EXTRA_LOOT_TABLES, Constants.NBT.TAG_STRING);

		for (int i = 0; i < tagList.tagCount(); i++) {
			String lootTableId = tagList.getStringTagAt(i);
			LootTable lootTable = entity.world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(lootTableId));
			LootContext.Builder builder = new LootContext.Builder((WorldServer) entity.world);
			builder.withLootedEntity(entity);
			builder.withDamageSource(source);

			if (recentlyHit && entity instanceof EntityLivingBase) {
				EntityPlayer attackingPlayer = ((EntityLivingBase) entity).attackingPlayer;

				if (attackingPlayer != null) {
					builder.withPlayer(attackingPlayer);
					builder.withLuck(attackingPlayer.getLuck());
				}
			}

			LootContext lootContext = builder.build();
			List<ItemStack> itemStackList = lootTable.generateLootForPools(entity.world.rand, lootContext);

			for (ItemStack itemStack : itemStackList) {
				EntityItem entityItem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, itemStack);
				drops.add(entityItem);
			}
		}
	}
}
