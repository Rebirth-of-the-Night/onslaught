package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Responsible for denying the sleep check if any player has an active invasion.
 */
public class InvasionSleepInhibitorEventHandler {

  @SubscribeEvent
  public void on(SleepingTimeCheckEvent event) {

    EntityPlayer entityPlayer = event.getEntityPlayer();

    if (entityPlayer.world.isRemote) {
      return;
    }

    if (!(entityPlayer instanceof EntityPlayerMP)) {
      return;
    }

    MinecraftServer minecraftServer = entityPlayer.world.getMinecraftServer();

    if (minecraftServer == null) {
      return;
    }

    PlayerList playerList = minecraftServer.getPlayerList();
    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(entityPlayer.world);

    for (EntityPlayerMP player : playerList.getPlayers()) {
      InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

      if (playerData.getInvasionState() == InvasionPlayerData.EnumInvasionState.Active) {
        event.setResult(Event.Result.DENY);
        break;
      }
    }
  }
}