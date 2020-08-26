package com.codetaylor.mc.onslaught.modules.onslaught.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SCPacketMotion
    implements IMessage,
    IMessageHandler<SCPacketMotion, SCPacketMotion> {

  private int entityId;
  private double motionX;
  private double motionY;
  private double motionZ;

  @SuppressWarnings("unused")
  public SCPacketMotion() {
    // serialization
  }

  public SCPacketMotion(int entityId, double motionX, double motionY, double motionZ) {

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
  public SCPacketMotion onMessage(SCPacketMotion message, MessageContext ctx) {

    WorldClient world = Minecraft.getMinecraft().world;
    Entity entity = world.getEntityByID(message.entityId);

    if (entity != null) {
      entity.motionX += message.motionX;
      entity.motionY += message.motionY;
      entity.motionZ += message.motionZ;
    }

    return null;
  }
}
