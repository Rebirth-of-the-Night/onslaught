package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityAntiAir {

  @CapabilityInject(IAntiAirPlayerData.class)
  public static Capability<IAntiAirPlayerData> INSTANCE = null;

  public static IAntiAirPlayerData get(EntityPlayer player) {

    return player.getCapability(INSTANCE, null);
  }
}
