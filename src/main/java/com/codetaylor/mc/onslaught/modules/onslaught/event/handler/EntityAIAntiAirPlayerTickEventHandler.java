package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityAntiAir;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IAntiAirPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketAntiAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Responsible for updating each player's motionY.
 */
public class EntityAIAntiAirPlayerTickEventHandler {

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

      IAntiAirPlayerData data = CapabilityAntiAir.get(entityPlayer);
      double dataMotionY = data.getMotionY();

      if (dataMotionY != 0) {
        data.setTicksOffGround(data.getTicksOffGround() + 1);

        if (data.getTicksOffGround() >= ModuleOnslaughtConfig.CUSTOM_AI.ANTI_AIR.DELAY_TICKS) {
          SCPacketAntiAir packet = new SCPacketAntiAir(entityPlayer.getEntityId(), 0, dataMotionY, 0);
          ModuleOnslaught.PACKET_SERVICE.sendTo(packet, (EntityPlayerMP) entityPlayer);
          data.setMotionY(0);
        }
      }

      if (entityPlayer.onGround) {
        data.setTicksOffGround(0);
      }
    }
  }
}
