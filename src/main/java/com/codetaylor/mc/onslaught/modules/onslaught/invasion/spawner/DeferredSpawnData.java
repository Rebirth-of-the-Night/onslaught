package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateWave;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

/**
 * Responsible for containing all the data necessary to perform a deferred spawn.
 */
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

  @Override
  public String toString() {

    return "DeferredSpawnData{" +
        "entityLiving=" + this.entityLiving +
        ", dimensionId=" + this.dimensionId +
        ", pos=" + this.pos +
        ", uuid=" + this.uuid +
        ", waveIndex=" + this.waveIndex +
        ", mobIndex=" + this.mobIndex +
        ", spawnType=" + this.spawnType +
        ", secondaryMob=" + this.secondaryMob +
        ", ticksRemaining=" + this.ticksRemaining +
        '}';
  }
}
