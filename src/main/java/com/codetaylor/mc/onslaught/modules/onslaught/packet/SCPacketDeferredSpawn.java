package com.codetaylor.mc.onslaught.modules.onslaught.packet;

import com.codetaylor.mc.athenaeum.util.RandomHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Responsible for spawning deferred spawn particles at the given location. */
public class SCPacketDeferredSpawn
    implements IMessage, IMessageHandler<SCPacketDeferredSpawn, IMessage> {

  private double x;
  private double y;
  private double z;

  @SuppressWarnings("unused")
  public SCPacketDeferredSpawn() {
    // serialization
  }

  public SCPacketDeferredSpawn(double x, double y, double z) {

    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public void toBytes(ByteBuf buf) {

    buf.writeDouble(this.x);
    buf.writeDouble(this.y);
    buf.writeDouble(this.z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {

    this.x = buf.readDouble();
    this.y = buf.readDouble();
    this.z = buf.readDouble();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public IMessage onMessage(SCPacketDeferredSpawn message, MessageContext ctx) {

    World world = Minecraft.getMinecraft().world;

    int range = 1;

    for (int i = 0; i < 16; i++) {
      double offsetX = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double offsetY = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double offsetZ = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double x = message.x;
      double y = message.y;
      double z = message.z;
      world.spawnParticle(
          EnumParticleTypes.PORTAL, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
    }

    for (int i = 0; i < 4; i++) {
      double offsetX = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double offsetY = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double offsetZ = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double x = message.x;
      double y = message.y;
      double z = message.z;
      world.spawnParticle(
          EnumParticleTypes.SMOKE_LARGE, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
    }

    for (int i = 0; i < 16; i++) {
      double offsetX = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double offsetY = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double offsetZ = (RandomHelper.random().nextDouble() * 2.0 - 1.0) * range;
      double x = message.x;
      double y = message.y;
      double z = message.z;
      world.spawnParticle(
          EnumParticleTypes.PORTAL, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
    }

    return null;
  }
}
