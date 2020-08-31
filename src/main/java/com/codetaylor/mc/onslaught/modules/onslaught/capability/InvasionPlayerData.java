package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class InvasionPlayerData
    implements IInvasionPlayerData,
    Capability.IStorage<IInvasionPlayerData> {

  @Nullable
  @Override
  public NBTBase writeNBT(Capability<IInvasionPlayerData> capability, IInvasionPlayerData instance, EnumFacing side) {

    NBTTagCompound tag = new NBTTagCompound();
    return tag;
  }

  @Override
  public void readNBT(Capability<IInvasionPlayerData> capability, IInvasionPlayerData instance, EnumFacing side, NBTBase nbt) {

    //
  }
}
