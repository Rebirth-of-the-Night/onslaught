package com.codetaylor.mc.onslaught.modules.onslaught.event.handler;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionKillCountUpdater;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
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
    World world = entity.world;

    if (world.isRemote) {
      return;
    }

    if (!(world instanceof WorldServer)) {
      return;
    }

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(world);
    NBTTagCompound entityData = entity.getEntityData();
    MinecraftServer minecraftServer = world.getMinecraftServer();
    // Checked above
    //noinspection ConstantConditions
    PlayerList playerList = minecraftServer.getPlayerList();
    this.invasionKillCountUpdater.onDeath(invasionGlobalSavedData, entityData, playerList::getPlayerByUUID);
  }
}