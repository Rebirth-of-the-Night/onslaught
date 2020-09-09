package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvasionPlayerData
    implements INBTSerializable<NBTTagCompound> {

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
  private InvasionData invasionData;

  public enum EnumInvasionState {

    /**
     * Player's timer is still ticking down.
     */
    Waiting(0),

    /**
     * Player's timer has expired and they have been flagged as eligible for
     * an invasion.
     */
    Eligible(1),

    /**
     * Player has been selected from the collection of eligible players and their
     * invasion data has been assigned.
     */
    Pending(2),

    /**
     * Player's invasion has begun and waves are spawning.
     */
    Active(3);

    private static final Int2ObjectMap<EnumInvasionState> MAP;

    static {
      EnumInvasionState[] states = EnumInvasionState.values();
      MAP = new Int2ObjectOpenHashMap<>(states.length);

      for (EnumInvasionState state : states) {
        MAP.put(state.id, state);
      }
    }

    private final int id;

    EnumInvasionState(int id) {

      this.id = id;
    }

    public int getId() {

      return this.id;
    }

    public static EnumInvasionState from(int id) {

      if (!MAP.containsKey(id)) {
        throw new IllegalArgumentException("Unknown id for state: " + id);
      }

      return MAP.get(id);
    }
  }

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

  public int getTicksUntilEligible() {

    return this.ticksUntilEligible;
  }

  public void setTicksUntilEligible(int ticksUntilEligible) {

    this.ticksUntilEligible = ticksUntilEligible;
  }

  public EnumInvasionState getInvasionState() {

    return this.invasionState;
  }

  public void setInvasionState(EnumInvasionState invasionState) {

    this.invasionState = invasionState;
  }

  @Nullable
  public InvasionData getInvasionData() {

    return this.invasionData;
  }

  public void setInvasionData(@Nullable InvasionData invasionData) {

    this.invasionData = invasionData;
  }

  public boolean hasInvasionData() {

    return (this.invasionData != null);
  }

  // ---------------------------------------------------------------------------
  // - Serialization
  // ---------------------------------------------------------------------------

  @Override
  public NBTTagCompound serializeNBT() {

    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger("ticksUntilPendingInvasion", this.ticksUntilEligible);
    tag.setInteger("invasionState", this.invasionState.getId());

    if (this.invasionData != null) {
      tag.setTag("invasionData", this.invasionData.serializeNBT());
    }

    return tag;
  }

  @Override
  public void deserializeNBT(NBTTagCompound tag) {

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
    private String invasionTemplateId;

    /**
     * The timestamp after which the invasion becomes active.
     */
    private long timestamp;

    /**
     * The invasion's wave data.
     */
    private final List<WaveData> waveDataList = new ArrayList<>();

    public String getInvasionTemplateId() {

      return this.invasionTemplateId;
    }

    public void setInvasionTemplateId(String invasionTemplateId) {

      this.invasionTemplateId = invasionTemplateId;
    }

    public long getTimestamp() {

      return this.timestamp;
    }

    public void setTimestamp(long timestamp) {

      this.timestamp = timestamp;
    }

    public List<WaveData> getWaveDataList() {

      return this.waveDataList;
    }

    @Override
    public NBTTagCompound serializeNBT() {

      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("invasionTemplateId", this.invasionTemplateId);
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

      this.invasionTemplateId = tag.getString("invasionTemplateId");
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

      private int delayTicks = 0;

      private List<MobData> mobDataList = new ArrayList<>();

      public int getDelayTicks() {

        return this.delayTicks;
      }

      public void setDelayTicks(int delayTicks) {

        this.delayTicks = delayTicks;
      }

      public List<MobData> getMobDataList() {

        return this.mobDataList;
      }

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

      private String mobTemplateId;
      private int totalCount;
      private int killedCount;
      private SpawnData spawnData;

      public String getMobTemplateId() {

        return this.mobTemplateId;
      }

      public void setMobTemplateId(String mobTemplateId) {

        this.mobTemplateId = mobTemplateId;
      }

      public int getTotalCount() {

        return this.totalCount;
      }

      public void setTotalCount(int totalCount) {

        this.totalCount = totalCount;
      }

      public int getKilledCount() {

        return this.killedCount;
      }

      public void setKilledCount(int killedCount) {

        this.killedCount = killedCount;
      }

      public SpawnData getSpawnData() {

        return this.spawnData;
      }

      public void setSpawnData(SpawnData spawnData) {

        this.spawnData = spawnData;
      }

      @Override
      public NBTTagCompound serializeNBT() {

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("mobTemplateId", this.mobTemplateId);
        tag.setInteger("totalCount", this.totalCount);
        tag.setInteger("killedCount", this.killedCount);
        tag.setTag("spawnData", this.spawnData.serializeNBT());
        return tag;
      }

      @Override
      public void deserializeNBT(NBTTagCompound tag) {

        this.mobTemplateId = tag.getString("mobTemplateId");
        this.totalCount = tag.getInteger("totalCount");
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

      public SpawnData copy() {

        SpawnData copy = new SpawnData();
        copy.type = this.type;
        copy.light = Arrays.copyOf(this.light, this.light.length);
        copy.force = this.force;
        copy.rangeXZ = Arrays.copyOf(this.rangeXZ, this.rangeXZ.length);
        copy.rangeY = this.rangeY;
        copy.stepRadius = this.stepRadius;
        copy.sampleDistance = this.sampleDistance;
        return copy;
      }

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