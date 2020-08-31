package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityInvasion {

  @CapabilityInject(IInvasionPlayerData.class)
  public static Capability<IInvasionPlayerData> INSTANCE = null;

  public static IInvasionPlayerData get(EntityPlayer player) {

    return player.getCapability(INSTANCE, null);
  }
}
