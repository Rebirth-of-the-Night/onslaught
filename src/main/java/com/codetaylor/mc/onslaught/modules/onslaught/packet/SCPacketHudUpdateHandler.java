package com.codetaylor.mc.onslaught.modules.onslaught.packet;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.InvasionHudRenderInfo;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.client.InvasionHudRenderInfoComparator;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

/**
 * Responsible for updating the shared render info list when a packet is received.
 */
public class SCPacketHudUpdateHandler
    implements IMessageHandler<SCPacketHudUpdate, SCPacketHudUpdate> {

  private final List<InvasionHudRenderInfo> invasionHudRenderInfoList;

  public SCPacketHudUpdateHandler(List<InvasionHudRenderInfo> invasionHudRenderInfoList) {

    this.invasionHudRenderInfoList = invasionHudRenderInfoList;
  }

  @Override
  public SCPacketHudUpdate onMessage(SCPacketHudUpdate message, MessageContext ctx) {

    this.invasionHudRenderInfoList.clear();
    List<InvasionHudRenderInfo> invasionHudRenderInfoList = message.getInvasionHudRenderInfoList();
    List<InvasionHudRenderInfo> sortedCopy = InvasionHudRenderInfoComparator.ORDERING_INSTANCE.sortedCopy(invasionHudRenderInfoList);
    this.invasionHudRenderInfoList.addAll(sortedCopy);
    return null;
  }
}