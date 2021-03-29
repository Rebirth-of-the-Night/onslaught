package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityAntiAirProvider implements ICapabilitySerializable<NBTTagCompound> {

  private final AntiAirPlayerData data;

  public CapabilityAntiAirProvider() {

    this.data = new AntiAirPlayerData();
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {

    return (capability == CapabilityAntiAir.INSTANCE);
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {

    if (capability == CapabilityAntiAir.INSTANCE) {
      //noinspection unchecked
      return (T) this.data;
    }

    return null;
  }

  @Override
  public NBTTagCompound serializeNBT() {

    return (NBTTagCompound) this.data.writeNBT(CapabilityAntiAir.INSTANCE, this.data, null);
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {

    this.data.readNBT(CapabilityAntiAir.INSTANCE, this.data, null, nbt);
  }
}
