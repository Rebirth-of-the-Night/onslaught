package com.codetaylor.mc.onslaught.modules.onslaught.packet;

import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityAntiAir;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IAntiAirPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet sent to the client containing relative motion values. Updates the player's motion values
 * and sets a value on the player's anti-air capability.
 */
public class SCPacketAntiAir
    implements IMessage, IMessageHandler<SCPacketAntiAir, SCPacketAntiAir> {

  private int entityId;
  private double motionX;
  private double motionY;
  private double motionZ;

  @SuppressWarnings("unused")
  public SCPacketAntiAir() {
    // serialization
  }

  public SCPacketAntiAir(int entityId, double motionX, double motionY, double motionZ) {

    this.entityId = entityId;
    this.motionX = motionX;
    this.motionY = motionY;
    this.motionZ = motionZ;
  }

  @Override
  public void fromBytes(ByteBuf buf) {

    this.entityId = buf.readInt();
    this.motionX = buf.readDouble();
    this.motionY = buf.readDouble();
    this.motionZ = buf.readDouble();
  }

  @Override
  public void toBytes(ByteBuf buf) {

    buf.writeInt(this.entityId);
    buf.writeDouble(this.motionX);
    buf.writeDouble(this.motionY);
    buf.writeDouble(this.motionZ);
  }

  @Override
  public SCPacketAntiAir onMessage(SCPacketAntiAir message, MessageContext ctx) {

    WorldClient world = Minecraft.getMinecraft().world;
    Entity entity = world.getEntityByID(message.entityId);

    if (entity != null) {
      entity.motionX += message.motionX;
      entity.motionY += message.motionY;
      entity.motionZ += message.motionZ;

      if (entity instanceof EntityPlayer) {
        IAntiAirPlayerData data = CapabilityAntiAir.get((EntityPlayer) entity);
        data.setMotionY(message.motionY);
      }
    }

    return null;
  }
}
