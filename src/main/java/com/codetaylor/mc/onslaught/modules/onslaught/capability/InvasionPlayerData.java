package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class InvasionPlayerData
    implements IInvasionPlayerData,
    Capability.IStorage<IInvasionPlayerData> {

  /**
   * This is the number of ticks left until the player is flagged as pending an
   * invasion.
   */
  private int ticksUntilEligible;

  /**
   * This holds the state of the player's invasion.
   */
  private EnumInvasionState invasionState;

  /**
   * The data for the player's invasion.
   */
  @Nullable
  private InvasionData invasionData;

  public InvasionPlayerData() {

    // This needs to be set to an initial value or new players will get
    // an invasion immediately.
    int min = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[0];
    int max = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[1];
    min = Math.min(max, min);
    max = Math.max(max, min);
    this.ticksUntilEligible = RandomHelper.random().nextInt(max - min + 1) + min;

    this.invasionState = EnumInvasionState.Waiting;
  }

  // ---------------------------------------------------------------------------
  // - Accessors
  // ---------------------------------------------------------------------------

  @Override
  public int getTicksUntilEligible() {

    return this.ticksUntilEligible;
  }

  @Override
  public void setTicksUntilEligible(int ticksUntilEligible) {

    this.ticksUntilEligible = ticksUntilEligible;
  }

  @Override
  public EnumInvasionState getInvasionState() {

    return this.invasionState;
  }

  @Override
  public void setInvasionState(EnumInvasionState invasionState) {

    this.invasionState = invasionState;
  }

  @Override
  @Nullable
  public InvasionData getInvasionData() {

    return invasionData;
  }

  @Override
  public void setInvasionData(@Nullable InvasionData invasionData) {

    this.invasionData = invasionData;
  }

  @Override
  public boolean hasInvasionData() {

    return (this.invasionData != null);
  }

  // ---------------------------------------------------------------------------
  // - Serialization
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public NBTBase writeNBT(Capability<IInvasionPlayerData> capability, IInvasionPlayerData instance, EnumFacing side) {

    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger("ticksUntilPendingInvasion", this.ticksUntilEligible);
    tag.setInteger("invasionState", this.invasionState.getId());

    if (this.invasionData != null) {
      tag.setTag("invasionData", this.invasionData.serializeNBT());
    }

    return tag;
  }

  @Override
  public void readNBT(Capability<IInvasionPlayerData> capability, IInvasionPlayerData instance, EnumFacing side, NBTBase nbt) {

    NBTTagCompound tag = (NBTTagCompound) nbt;
    this.ticksUntilEligible = tag.getInteger("ticksUntilPendingInvasion");
    this.invasionState = EnumInvasionState.from(tag.getInteger("invasionState"));

    if (tag.hasKey("invasionData")) {
      this.invasionData = new InvasionData();
      this.invasionData.deserializeNBT(tag.getCompoundTag("invasionData"));
    }
  }

  // ---------------------------------------------------------------------------
  // - Invasion Data
  // ---------------------------------------------------------------------------

  public static class InvasionData
      implements INBTSerializable<NBTTagCompound> {

    /**
     * The template id of the invasion.
     */
    public String id;

    /**
     * The timestamp after which the invasion becomes active.
     */
    public long timestamp;

    /**
     * The invasion's wave data.
     */
    public List<WaveData> waveDataList = new ArrayList<>();

    @Override
    public NBTTagCompound serializeNBT() {

      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("id", this.id);
      tag.setLong("timestamp", this.timestamp);

      NBTTagList tagList = new NBTTagList();

      for (WaveData waveData : this.waveDataList) {
        tagList.appendTag(waveData.serializeNBT());
      }

      tag.setTag("waveDataList", tagList);

      return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {

      this.id = tag.getString("id");
      this.timestamp = tag.getLong("timestamp");

      NBTTagList waveDataList = tag.getTagList("waveDataList", Constants.NBT.TAG_COMPOUND);

      for (NBTBase nbt : waveDataList) {
        WaveData waveData = new WaveData();
        waveData.deserializeNBT((NBTTagCompound) nbt);
        this.waveDataList.add(waveData);
      }
    }

    public static class WaveData
        implements INBTSerializable<NBTTagCompound> {

      public int delayTicks = 0;

      public List<MobData> mobDataList = new ArrayList<>();

      @Override
      public NBTTagCompound serializeNBT() {

        NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("delayTicks", this.delayTicks);

        NBTTagList tagList = new NBTTagList();

        for (MobData mobData : this.mobDataList) {
          tagList.appendTag(mobData.serializeNBT());
        }

        tag.setTag("mobDataList", tagList);

        return tag;
      }

      @Override
      public void deserializeNBT(NBTTagCompound tag) {

        this.delayTicks = tag.getInteger("delayTicks");

        NBTTagList mobDataList = tag.getTagList("mobDataList", Constants.NBT.TAG_COMPOUND);

        for (NBTBase nbt : mobDataList) {
          MobData mobData = new MobData();
          mobData.deserializeNBT((NBTTagCompound) nbt);
          this.mobDataList.add(mobData);
        }
      }
    }

    public static class MobData
        implements INBTSerializable<NBTTagCompound> {

      public String id;
      public int count;

      public int remainingSpawnCount;
      public int killedCount;

      public SpawnData spawnData;

      @Override
      public NBTTagCompound serializeNBT() {

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("id", this.id);
        tag.setInteger("count", this.count);
        tag.setInteger("remainingSpawnCount", this.remainingSpawnCount);
        tag.setInteger("killedCount", this.killedCount);
        tag.setTag("spawnData", this.spawnData.serializeNBT());
        return tag;
      }

      @Override
      public void deserializeNBT(NBTTagCompound tag) {

        this.id = tag.getString("id");
        this.count = tag.getInteger("count");
        this.remainingSpawnCount = tag.getInteger("remainingSpawnCount");
        this.killedCount = tag.getInteger("killedCount");
        this.spawnData = new SpawnData();
        this.spawnData.deserializeNBT(tag.getCompoundTag("spawnData"));
      }
    }

    public static class SpawnData
        implements INBTSerializable<NBTTagCompound> {

      public InvasionTemplateWave.EnumSpawnType type;
      public int[] light;
      public boolean force;
      public int[] rangeXZ;
      public int rangeY;
      public int stepRadius;
      public int sampleDistance;

      @Override
      public NBTTagCompound serializeNBT() {

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", this.type.name());
        tag.setIntArray("light", this.light);
        tag.setBoolean("force", this.force);
        tag.setIntArray("rangeXZ", this.rangeXZ);
        tag.setInteger("rangeY", this.rangeY);
        tag.setInteger("stepRadius", this.stepRadius);
        tag.setInteger("sampleDistance", this.sampleDistance);
        return tag;
      }

      @Override
      public void deserializeNBT(NBTTagCompound tag) {

        this.type = InvasionTemplateWave.EnumSpawnType.valueOf(tag.getString("type"));
        this.light = tag.getIntArray("light");
        this.force = tag.getBoolean("force");
        this.rangeXZ = tag.getIntArray("rangeXZ");
        this.rangeY = tag.getInteger("rangeY");
        this.stepRadius = tag.getInteger("stepRadius");
        this.sampleDistance = tag.getInteger("sampleDistance");
      }
    }
  }

}
