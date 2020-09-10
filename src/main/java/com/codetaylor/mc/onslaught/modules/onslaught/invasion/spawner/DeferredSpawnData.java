package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class DeferredSpawnData {

  private final EntityLiving entityLiving;
  private final int dimensionId;
  private final BlockPos pos;
  private final UUID uuid;
  private final int waveIndex;
  private final int mobIndex;
  private final InvasionTemplateWave.EnumSpawnType spawnType;
  private final InvasionTemplateWave.SecondaryMob secondaryMob;

  private int ticksRemaining;

  public DeferredSpawnData(
      EntityLiving entityLiving,
      int dimensionId,
      BlockPos pos,
      UUID uuid,
      int waveIndex,
      int mobIndex,
      InvasionTemplateWave.EnumSpawnType spawnType,
      InvasionTemplateWave.SecondaryMob secondaryMob,
      int ticksRemaining
  ) {

    this.entityLiving = entityLiving;
    this.dimensionId = dimensionId;
    this.pos = pos;
    this.uuid = uuid;
    this.waveIndex = waveIndex;
    this.mobIndex = mobIndex;
    this.spawnType = spawnType;
    this.secondaryMob = secondaryMob;
    this.ticksRemaining = ticksRemaining;
  }

  public EntityLiving getEntityLiving() {

    return this.entityLiving;
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

  public InvasionTemplateWave.SecondaryMob getSecondaryMob() {

    return this.secondaryMob;
  }

  public int getTicksRemaining() {

    return this.ticksRemaining;
  }

  public void setTicksRemaining(int ticksRemaining) {

    this.ticksRemaining = ticksRemaining;
  }

  public InvasionTemplateWave.EnumSpawnType getSpawnType() {

    return this.spawnType;
  }
}
