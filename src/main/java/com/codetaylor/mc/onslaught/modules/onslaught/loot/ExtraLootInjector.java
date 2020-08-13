package com.codetaylor.mc.onslaught.modules.onslaught.loot;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Level;

/**
 * Responsible for adding loot from the extra tables to the loot list when a mob dies.
 */
public class ExtraLootInjector {

  private static final MethodHandle entityLiving$attackingPlayerGetter;

  static {

    MethodHandle methodHandle;

    try {
      methodHandle = MethodHandles.lookup().unreflectGetter(
          /*
          MC 1.12: net/minecraft/entity/EntityLivingBase.attackingPlayer
          Name: aS => field_70717_bb => attackingPlayer
          Comment: The most recent player that has attacked this entity
          Side: BOTH
          AT: public net.minecraft.entity.EntityLivingBase field_70717_bb # attackingPlayer
           */
          ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_70717_bb")
      );

    } catch (Exception e) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error unreflecting getter for field_70717_bb");
      methodHandle = null;
    }

    entityLiving$attackingPlayerGetter = methodHandle;
  }

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

      try {

        if (recentlyHit
            && entityLiving$attackingPlayerGetter != null) {
          EntityPlayer attackingPlayer = (EntityPlayer) entityLiving$attackingPlayerGetter.invokeExact((EntityLivingBase) entity);

          if (attackingPlayer != null) {
            builder.withPlayer(attackingPlayer);
            builder.withLuck(attackingPlayer.getLuck());
          }
        }

      } catch (Throwable throwable) {
        ModOnslaught.LOG.log(Level.SEVERE, "Error invoking unreflected getter for field_70717_bb");
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