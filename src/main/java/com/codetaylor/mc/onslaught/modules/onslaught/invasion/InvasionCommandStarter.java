package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/** Responsible for starting an invasion immediately. Used by commands. */
public class InvasionCommandStarter {

  private final InvasionPlayerDataFactory invasionPlayerDataFactory;
  private final Set<UUID> eligiblePlayers;

  public InvasionCommandStarter(
      InvasionPlayerDataFactory invasionPlayerDataFactory, Set<UUID> eligiblePlayers) {

    this.invasionPlayerDataFactory = invasionPlayerDataFactory;
    this.eligiblePlayers = eligiblePlayers;
  }

  /**
   * Starts the given invasion for the given player immediately. The templateId must be verified
   * before calling this method.
   *
   * @param templateId the id of the invasion to start
   * @param player the player to start the invasion for
   * @return false if the player already has an active invasion, else true
   */
  public boolean startInvasionForPlayer(String templateId, EntityPlayerMP player) {

    World world = player.world;
    InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(world);
    UUID uuid = player.getUniqueID();
    InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(uuid);
    InvasionPlayerData.EnumInvasionState invasionState = data.getInvasionState();

    // Skip players with an active invasion.
    if (invasionState == InvasionPlayerData.EnumInvasionState.Active) {
      return false;
    }

    InvasionPlayerData.InvasionData invasionData =
        this.invasionPlayerDataFactory.create(
            templateId, UUID.randomUUID(), player.getRNG(), 0,-1, -1);
    data.setInvasionState(InvasionPlayerData.EnumInvasionState.Active);
    data.setInvasionData(invasionData);
    invasionGlobalSavedData.markDirty();

    // Remove the player from the eligible set
    this.eligiblePlayers.remove(uuid);

    MinecraftForge.EVENT_BUS.post(
        new InvasionStateChangedEvent(
            player, invasionState, InvasionPlayerData.EnumInvasionState.Active));

    return true;
  }
}
