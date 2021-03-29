package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionMessageSender;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Responsible for triggering the begin and end messages in response to events. */
public class InvasionMessageEventHandler {

  private final InvasionMessageSender invasionMessageSenderBegin;
  private final InvasionMessageSender invasionMessageSenderEnd;

  public InvasionMessageEventHandler(
      InvasionMessageSender invasionMessageSenderBegin,
      InvasionMessageSender invasionMessageSenderEnd) {

    this.invasionMessageSenderBegin = invasionMessageSenderBegin;
    this.invasionMessageSenderEnd = invasionMessageSenderEnd;
  }

  @SubscribeEvent
  public void on(InvasionStateChangedEvent event) {

    if (event.getCurrentState() == InvasionPlayerData.EnumInvasionState.Active) {
      this.invasionMessageSenderBegin.sendMessage(event.getPlayer());

    } else if (event.getCurrentState() == InvasionPlayerData.EnumInvasionState.Waiting) {
      this.invasionMessageSenderEnd.sendMessage(event.getPlayer());
    }
  }
}
