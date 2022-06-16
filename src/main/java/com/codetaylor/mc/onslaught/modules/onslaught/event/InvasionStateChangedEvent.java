package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

/** Fired on the server when an invasion state transitions. */
public class InvasionStateChangedEvent extends Event {

  private final EntityPlayerMP player;
  private final InvasionPlayerData.EnumInvasionState previousState;
  private final InvasionPlayerData.EnumInvasionState currentState;

  public InvasionStateChangedEvent(
      EntityPlayerMP player,
      InvasionPlayerData.EnumInvasionState previousState,
      InvasionPlayerData.EnumInvasionState currentState) {

    this.player = player;
    this.previousState = previousState;
    this.currentState = currentState;
  }

  public EntityPlayerMP getPlayer() {

    return this.player;
  }

  public InvasionPlayerData.EnumInvasionState getPreviousState() {

    return this.previousState;
  }

  public InvasionPlayerData.EnumInvasionState getCurrentState() {

    return this.currentState;
  }
}
