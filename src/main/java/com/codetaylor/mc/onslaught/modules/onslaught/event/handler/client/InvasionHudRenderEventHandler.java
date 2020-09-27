package com.codetaylor.mc.onslaught.modules.onslaught.event.handler.client;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.client.InvasionHudRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Responsible for calling the render method on the {@link InvasionHudRenderer}.
 */
public class InvasionHudRenderEventHandler {

  private final InvasionHudRenderer invasionHudRenderer;

  public InvasionHudRenderEventHandler(InvasionHudRenderer invasionHudRenderer) {

    this.invasionHudRenderer = invasionHudRenderer;
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void on(RenderGameOverlayEvent.Post event) {

    if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
      this.invasionHudRenderer.render();
    }
  }
}