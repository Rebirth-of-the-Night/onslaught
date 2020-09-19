package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.Tag;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionEntityKilledEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.UUID;

/**
 * Responsible for incrementing a player's kill count in their invasion data.
 */
public class InvasionKillCountUpdater {

  public void onDeath(InvasionGlobalSavedData invasionGlobalSavedData, NBTTagCompound entityData) {

    if (!entityData.hasKey(Tag.ONSLAUGHT)) {
      return;
    }

    NBTTagCompound modTag = entityData.getCompoundTag(Tag.ONSLAUGHT);

    if (!modTag.hasKey(Tag.INVASION_DATA)) {
      return;
    }

    NBTTagCompound invasionTag = modTag.getCompoundTag(Tag.INVASION_DATA);

    int waveIndex = invasionTag.getInteger(Tag.INVASION_WAVE_INDEX);
    int mobIndex = invasionTag.getInteger(Tag.INVASION_MOB_INDEX);
    String playerUuidString = invasionTag.getString(Tag.INVASION_PLAYER_UUID);
    UUID playerUuid = UUID.fromString(playerUuidString);
    String invasionUuidString = invasionTag.getString(Tag.INVASION_UUID);
    UUID invasionUuid = UUID.fromString(invasionUuidString);

    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(playerUuid);
    InvasionPlayerData.InvasionData invasionData = playerData.getInvasionData();

    if (invasionData != null
        && invasionData.getInvasionUuid().equals(invasionUuid)) {
      List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.getWaveDataList();
      InvasionPlayerData.InvasionData.WaveData waveData = waveDataList.get(waveIndex);
      List<InvasionPlayerData.InvasionData.MobData> mobDataList = waveData.getMobDataList();
      InvasionPlayerData.InvasionData.MobData mobData = mobDataList.get(mobIndex);
      int killedCount = mobData.getKilledCount();
      mobData.setKilledCount(killedCount + 1);
      invasionGlobalSavedData.markDirty();

      if (ModuleOnslaughtConfig.DEBUG.INVASION_DATA_UPDATES) {
        String message = String.format("Kill count updated for wave index %d mob id %s from %d to %d", waveIndex, mobData.getMobTemplateId(), killedCount, killedCount + 1);
        ModOnslaught.LOG.fine(message);
        System.out.println(message);
      }

      MinecraftForge.EVENT_BUS.post(new InvasionEntityKilledEvent());
    }
  }
}