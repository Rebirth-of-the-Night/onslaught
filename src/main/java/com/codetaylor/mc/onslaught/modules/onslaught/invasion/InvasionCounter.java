package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class InvasionCounter {

  public int count(Function<UUID, InvasionPlayerData.EnumInvasionState> invasionStateFunction, List<EntityPlayerMP> players) {

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
