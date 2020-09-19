package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Responsible for cancelling a player travelling to a new dimension if they
 * have an active invasion.
 */
public class InvasionDimensionTravelRestrictionEventHandler {

  @SubscribeEvent
  public void on(EntityTravelToDimensionEvent event) {

    Entity entity = event.getEntity();

    if (entity.world.isRemote) {
      return;
    }

    if (!(entity instanceof EntityPlayerMP)) {
      return;
    }

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(entity.world);
    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(entity.getUniqueID());
    event.setCanceled(playerData.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active);
  }
}