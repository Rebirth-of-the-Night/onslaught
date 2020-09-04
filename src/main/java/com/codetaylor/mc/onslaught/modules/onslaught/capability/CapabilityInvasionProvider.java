package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityInvasionProvider
    implements ICapabilitySerializable<NBTTagCompound> {

  private final InvasionPlayerData data;

  public CapabilityInvasionProvider() {

    this.data = new InvasionPlayerData();
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {

    return (capability == CapabilityInvasion.INSTANCE);
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {

    if (capability == CapabilityInvasion.INSTANCE) {
      //noinspection unchecked
      return (T) this.data;
    }

    return null;
  }

  @Override
  public NBTTagCompound serializeNBT() {

    return (NBTTagCompound) this.data.writeNBT(CapabilityInvasion.INSTANCE, this.data, null);
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {

    this.data.readNBT(CapabilityInvasion.INSTANCE, this.data, null, nbt);
  }
}
