package com.codetaylor.mc.onslaught.modules.onslaught.event.handler.client;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityAntiAir;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IAntiAirPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Responsible for preventing a player from sneaking on a ladder if being
 * pulled down by AntiAir.
 */
public class EntityAIAntiAirClientEventHandler {

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void on(InputUpdateEvent event) {

    MovementInput movementInput = event.getMovementInput();
    EntityPlayerSP player = Minecraft.getMinecraft().player;
    IAntiAirPlayerData data = CapabilityAntiAir.get(player);

    if (player.isOnLadder() && data.getMotionY() != 0) {
      movementInput.sneak = false;
      data.setMotionY(0);
    }
  }
}