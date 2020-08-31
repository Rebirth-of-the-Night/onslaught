package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Responsible for updating each player's invasion timer and notifying the
 * {@link InvasionManager} when a player is eligible for an invasion.
 */
public class InvasionPlayerTickEventHandler {

  private final InvasionManager invasionManager;

  public InvasionPlayerTickEventHandler(InvasionManager invasionManager) {

    this.invasionManager = invasionManager;
  }

  @SubscribeEvent
  public void on(TickEvent.PlayerTickEvent event) {

    if (event.phase == TickEvent.Phase.END) {

      EntityPlayer entityPlayer = event.player;

      if (entityPlayer.world.isRemote) {
        return;
      }

      if (!(entityPlayer instanceof EntityPlayerMP)) {
        return;
      }

      IInvasionPlayerData data = CapabilityInvasion.get(entityPlayer);
      int ticksUntilNextInvasion = data.getTicksUntilNextInvasion();

      if (ticksUntilNextInvasion <= 0) {
        this.invasionManager.notifyPlayerEligibleForInvasion(entityPlayer);

      } else {
        data.setTicksUntilNextInvasion(ticksUntilNextInvasion - 1);
      }
    }
  }
}
