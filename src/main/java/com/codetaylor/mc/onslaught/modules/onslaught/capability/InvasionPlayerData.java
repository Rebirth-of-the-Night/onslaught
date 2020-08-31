package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class InvasionPlayerData
    implements IInvasionPlayerData,
    Capability.IStorage<IInvasionPlayerData> {

  private int ticksUntilNextInvasion;

  @Override
  public int getTicksUntilNextInvasion() {

    return this.ticksUntilNextInvasion;
  }

  @Override
  public void setTicksUntilNextInvasion(int ticksUntilNextInvasion) {

    this.ticksUntilNextInvasion = ticksUntilNextInvasion;
  }

  @Nullable
  @Override
  public NBTBase writeNBT(Capability<IInvasionPlayerData> capability, IInvasionPlayerData instance, EnumFacing side) {

    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger("ticksUntilNextInvasion", this.ticksUntilNextInvasion);
    return tag;
  }

  @Override
  public void readNBT(Capability<IInvasionPlayerData> capability, IInvasionPlayerData instance, EnumFacing side, NBTBase nbt) {

    NBTTagCompound tag = (NBTTagCompound) nbt;
    this.ticksUntilNextInvasion = tag.getInteger("ticksUntilNextInvasion");
  }
}
