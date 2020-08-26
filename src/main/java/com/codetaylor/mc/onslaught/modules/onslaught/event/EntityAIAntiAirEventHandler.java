package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.AntiAirPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityAntiAir;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IAntiAirPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketMotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Responsible for attaching the AntiAir data capability and updating each
 * player's motionY.
 */
public class EntityAIAntiAirEventHandler {

  @SubscribeEvent
  public void on(LivingEvent.LivingUpdateEvent event) {

    Entity entity = event.getEntity();

    if (entity.world.isRemote) {
      return;
    }

    if (!(entity instanceof EntityPlayerMP)) {
      return;
    }

    IAntiAirPlayerData data = CapabilityAntiAir.get((EntityPlayer) entity);
    double dataMotionY = data.getMotionY();

    if (dataMotionY != 0) {
      data.setTicksOffGround(data.getTicksOffGround() + 1);

      if (data.getTicksOffGround() >= ModuleOnslaughtConfig.CUSTOM_AI.ANTI_AIR.DELAY_TICKS) {
        SCPacketMotion packet = new SCPacketMotion(entity.getEntityId(), 0, dataMotionY, 0);
        ModuleOnslaught.PACKET_SERVICE.sendTo(packet, (EntityPlayerMP) entity);
        data.setMotionY(0);
      }
    }

    if (entity.onGround) {
      data.setTicksOffGround(0);
    }
  }

  @SubscribeEvent
  public void on(AttachCapabilitiesEvent<Entity> event) {

    Entity entity = event.getObject();

    if (entity instanceof EntityPlayer && !(entity instanceof FakePlayer)) {
      event.addCapability(new ResourceLocation(ModuleOnslaught.MOD_ID, "anti_air"), new Provider());
    }
  }

  private static class Provider
      implements ICapabilitySerializable<NBTTagCompound> {

    private final AntiAirPlayerData data;

    /* package */ Provider() {

      this.data = new AntiAirPlayerData();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {

      return (capability == CapabilityAntiAir.INSTANCE);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {

      if (capability == CapabilityAntiAir.INSTANCE) {
        //noinspection unchecked
        return (T) this.data;
      }

      return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {

      return (NBTTagCompound) this.data.writeNBT(CapabilityAntiAir.INSTANCE, this.data, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

      this.data.readNBT(CapabilityAntiAir.INSTANCE, this.data, null, nbt);
    }
  }

}
