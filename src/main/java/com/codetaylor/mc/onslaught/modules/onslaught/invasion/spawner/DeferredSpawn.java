package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class DeferredSpawnData {

  private final int dimensionId;
  private final BlockPos pos;
  private final UUID uuid;
  private final int waveIndex;
  private final int mobIndex;
  private final String mobTemplateId;
  private final InvasionPlayerData.InvasionData.SpawnData spawnData;

  private int ticksRemaining;

  public DeferredSpawnData(
      int dimensionId,
      BlockPos pos,
      UUID uuid,
      int waveIndex,
      int mobIndex,
      String mobTemplateId,
      InvasionPlayerData.InvasionData.SpawnData spawnData,
      int ticksRemaining
  ) {

    this.dimensionId = dimensionId;
    this.pos = pos;
    this.uuid = uuid;
    this.waveIndex = waveIndex;
    this.mobIndex = mobIndex;
    this.mobTemplateId = mobTemplateId;
    this.spawnData = spawnData;
    this.ticksRemaining = ticksRemaining;
  }

  public int getDimensionId() {

    return this.dimensionId;
  }

  public BlockPos getPos() {

    return this.pos;
  }

  public UUID getUuid() {

    return this.uuid;
  }

  public int getWaveIndex() {

    return this.waveIndex;
  }

  public int getMobIndex() {

    return this.mobIndex;
  }

  public String getMobTemplateId() {

    return this.mobTemplateId;
  }

  public InvasionPlayerData.InvasionData.SpawnData getSpawnData() {

    return this.spawnData;
  }

  public int getTicksRemaining() {

    return this.ticksRemaining;
  }

  public void setTicksRemaining(int ticksRemaining) {

    this.ticksRemaining = ticksRemaining;
  }
}
