package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.LongSupplier;

public class EntityInvasionPeriodicWorldCleanup {

  private final LongSupplier offlineCleanupDelayTicksSupplier;
  private final EntityInvasionDataRemover entityInvasionDataRemover;

  public EntityInvasionPeriodicWorldCleanup(
      LongSupplier offlineCleanupDelayTicksSupplier,
      EntityInvasionDataRemover entityInvasionDataRemover
  ) {

    this.offlineCleanupDelayTicksSupplier = offlineCleanupDelayTicksSupplier;
    this.entityInvasionDataRemover = entityInvasionDataRemover;
  }

  public void cleanup(
      long worldTime,
      List<EntityLiving> entityLivingList,
      Function<UUID, EntityPlayerMP> uuidEntityPlayerFunction
  ) {

    if (entityLivingList.isEmpty()) {
      return;
    }

    for (EntityLiving entity : entityLivingList) {

      NBTTagCompound entityData = entity.getEntityData();
      NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

      if (!modTag.hasKey(Tag.INVASION_DATA)) {
        continue;
      }

      NBTTagCompound dataTag = modTag.getCompoundTag(Tag.INVASION_DATA);

      String playerUuidDataString = dataTag.getString(Tag.INVASION_PLAYER_UUID);
      UUID uuid = UUID.fromString(playerUuidDataString);

      EntityPlayerMP player = uuidEntityPlayerFunction.apply(uuid);

      if (player == null) {

        // player is offline, handle offline timer

        if (modTag.hasKey(Tag.INVASION_OFFLINE_TIMESTAMP)) {

          // check timestamp and remove if expired
          if (worldTime - modTag.getLong(Tag.INVASION_OFFLINE_TIMESTAMP) >= this.offlineCleanupDelayTicksSupplier.getAsLong()) {
            this.entityInvasionDataRemover.accept(entity);
          }

        } else {
          modTag.setLong(Tag.INVASION_OFFLINE_TIMESTAMP, worldTime);
        }

        continue;
      }

      // reset entity offline timestamp
      modTag.removeTag(Tag.INVASION_OFFLINE_TIMESTAMP);

      int playerDimensionId = player.world.provider.getDimension();
      int entityDimensionId = entity.world.provider.getDimension();

      if (playerDimensionId != entityDimensionId) {
        // player has died and is in a different dimension, cleanup immediately
        this.entityInvasionDataRemover.accept(entity);
        continue;
      }

      InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(player.world);
      InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());
      InvasionPlayerData.InvasionData invasionData = playerData.getInvasionData();

      if (playerData.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active
          || invasionData == null) {
        // player's invasion is no longer active, cleanup immediately
        this.entityInvasionDataRemover.accept(entity);
        continue;
      }

      UUID invasionUuid = invasionData.getInvasionUuid();
      String invasionUuidDataString = dataTag.getString(Tag.INVASION_UUID);
      UUID entityInvasionUuid = UUID.fromString(invasionUuidDataString);

      if (!invasionUuid.equals(entityInvasionUuid)) {
        // entity is from previous invasion, cleanup immediately
        this.entityInvasionDataRemover.accept(entity);
      }
    }
  }

}