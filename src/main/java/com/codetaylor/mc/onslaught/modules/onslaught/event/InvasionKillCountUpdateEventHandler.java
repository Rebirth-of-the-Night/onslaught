package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionKillCountUpdater;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Responsible for hooking the {@link LivingDeathEvent} and calling the
 * {@link InvasionKillCountUpdater}.
 */
public class InvasionKillCountUpdateEventHandler {

  private final InvasionKillCountUpdater invasionKillCountUpdater;

  public InvasionKillCountUpdateEventHandler(InvasionKillCountUpdater invasionKillCountUpdater) {

    this.invasionKillCountUpdater = invasionKillCountUpdater;
  }

  @SubscribeEvent
  public void on(LivingDeathEvent event) {

    EntityLivingBase entity = event.getEntityLiving();
    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(entity.world);
    NBTTagCompound entityData = entity.getEntityData();
    this.invasionKillCountUpdater.onDeath(invasionGlobalSavedData, entityData);
  }
}