package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

import java.util.UUID;

/**
 * Responsible for starting, stopping, and tracking invasions.
 */
public class InvasionManager {

  private final EligiblePlayerQueue eligiblePlayerQueue;

  public InvasionManager(EligiblePlayerQueue eligiblePlayerQueue) {

    this.eligiblePlayerQueue = eligiblePlayerQueue;
  }

  public void notifyPlayerEligibleForInvasion(EntityPlayer player) {

    UUID uuid = player.getUniqueID();
    this.eligiblePlayerQueue.add(uuid);
  }

  public void stopInvasionForPlayer(EntityPlayer player) {

    UUID uuid = player.getUniqueID();
    this.eligiblePlayerQueue.remove(uuid);
  }

  public void resetInvasionTimerForPlayer(EntityPlayer player) {

    IInvasionPlayerData data = CapabilityInvasion.get(player);
    int min = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[0];
    int max = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[1];
    data.setTicksUntilNextInvasion(RandomHelper.random().nextInt(max - min) + min);
  }

  public void update(WorldServer world) {

  }

}
