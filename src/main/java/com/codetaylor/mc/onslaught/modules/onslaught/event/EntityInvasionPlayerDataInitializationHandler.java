package com.codetaylor.mc.onslaught.modules.onslaught.event;

import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.IntSupplier;

/**
 * Responsible for initializing a player's invasion timer when they join for
 * the first time.
 */
public class EntityInvasionPlayerDataInitializationHandler {

  private final IntSupplier invasionPlayerTimerValueSupplier;

  public EntityInvasionPlayerDataInitializationHandler(IntSupplier invasionPlayerTimerValueSupplier) {

    this.invasionPlayerTimerValueSupplier = invasionPlayerTimerValueSupplier;
  }

  @SubscribeEvent
  public void on(EntityJoinWorldEvent event) {

    // Force run when an entity joins the world.

    World world = event.getWorld();

    if (world.isRemote) {
      return;
    }

    Entity entity = event.getEntity();

    if (!(entity instanceof EntityPlayerMP)) {
      return;
    }

    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(world);
    InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(entity.getUniqueID());
    int ticksUntilEligible = playerData.getTicksUntilEligible();

    if (ticksUntilEligible == Integer.MIN_VALUE) {
      playerData.setTicksUntilEligible(this.invasionPlayerTimerValueSupplier.getAsInt());
      invasionGlobalSavedData.markDirty();
    }
  }
}