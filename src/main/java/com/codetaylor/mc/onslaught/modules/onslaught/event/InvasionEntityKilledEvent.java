package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class InvasionEntityKilledEvent extends Event {

  private final EntityPlayerMP player;
  private final InvasionPlayerData.InvasionData invasionData;

  public InvasionEntityKilledEvent(
      EntityPlayerMP player, InvasionPlayerData.InvasionData invasionData) {

    this.player = player;
    this.invasionData = invasionData;
  }

  public EntityPlayerMP getPlayer() {

    return this.player;
  }

  public InvasionPlayerData.InvasionData getInvasionData() {

    return this.invasionData;
  }
}
