package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class AntiAirPlayerData
    implements IAntiAirPlayerData, Capability.IStorage<IAntiAirPlayerData> {

  private int ticksOffGround;
  private double motionY;

  @Override
  public double getMotionY() {

    return this.motionY;
  }

  @Override
  public void setMotionY(double motionY) {

    this.motionY = motionY;
  }

  @Override
  public int getTicksOffGround() {

    return this.ticksOffGround;
  }

  @Override
  public void setTicksOffGround(int value) {

    this.ticksOffGround = value;
  }

  // ---------------------------------------------------------------------------
  // - Serialization
  // -
  // - This data is temporary and doesn't need to be serialized.
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public NBTBase writeNBT(
      Capability<IAntiAirPlayerData> capability, IAntiAirPlayerData instance, EnumFacing side) {

    return new NBTTagCompound();
  }

  @Override
  public void readNBT(
      Capability<IAntiAirPlayerData> capability,
      IAntiAirPlayerData instance,
      EnumFacing side,
      NBTBase nbt) {
    //
  }
}
