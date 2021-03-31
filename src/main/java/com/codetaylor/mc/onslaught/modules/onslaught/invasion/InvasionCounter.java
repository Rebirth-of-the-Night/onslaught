package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayerMP;

/** Responsible for counting active invasions. */
public class InvasionCounter {

  public int count(
      Function<UUID, InvasionPlayerData.EnumInvasionState> invasionStateFunction,
      List<EntityPlayerMP> players) {

    int result = 0;

    for (EntityPlayerMP player : players) {
      UUID uuid = player.getUniqueID();
      InvasionPlayerData.EnumInvasionState invasionState = invasionStateFunction.apply(uuid);

      if (invasionState == InvasionPlayerData.EnumInvasionState.Pending
          || invasionState == InvasionPlayerData.EnumInvasionState.Active) {
        result += 1;
      }
    }

    return result;
  }
}
