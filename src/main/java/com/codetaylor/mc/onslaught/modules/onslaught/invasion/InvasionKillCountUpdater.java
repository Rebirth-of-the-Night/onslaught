package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.data.Tag;
import net.minecraft.nbt.NBTTagCompound;

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
    String uuidString = invasionTag.getString(Tag.INVASION_UUID);
    UUID uuid = UUID.fromString(uuidString);

    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(uuid);
    InvasionPlayerData.InvasionData invasionData = playerData.getInvasionData();

    if (invasionData != null) {
      List<InvasionPlayerData.InvasionData.WaveData> waveDataList = invasionData.getWaveDataList();
      InvasionPlayerData.InvasionData.WaveData waveData = waveDataList.get(waveIndex);
      List<InvasionPlayerData.InvasionData.MobData> mobDataList = waveData.getMobDataList();
      InvasionPlayerData.InvasionData.MobData mobData = mobDataList.get(mobIndex);
      int killedCount = mobData.getKilledCount();
      mobData.setKilledCount(killedCount + 1);
      invasionGlobalSavedData.markDirty();
    }
  }
}