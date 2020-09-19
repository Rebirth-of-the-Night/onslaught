package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.render.InvasionHudRenderInfo;
import com.codetaylor.mc.onslaught.modules.onslaught.packet.SCPacketHudUpdate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class InvasionClientHUDUpdateSender {

  private final IntSupplier rangeSupplier;
  private final InvasionCompletionPercentageCalculator invasionCompletionPercentageCalculator;

  public InvasionClientHUDUpdateSender(
      IntSupplier rangeSupplier,
      InvasionCompletionPercentageCalculator invasionCompletionPercentageCalculator
  ) {

    this.rangeSupplier = rangeSupplier;
    this.invasionCompletionPercentageCalculator = invasionCompletionPercentageCalculator;
  }

  public void update(List<EntityPlayerMP> players) {

    int range = this.rangeSupplier.getAsInt();
    int rangeSq = range * range;

    for (EntityPlayerMP player : players) {

      World world = player.world;

      List<EntityPlayerMP> playersNearby = world.getEntities(
          EntityPlayerMP.class,
          entity -> entity != null && entity.getDistanceSq(player) <= rangeSq
      );

      InvasionGlobalSavedData invasionGlobalSavedData = InvasionGlobalSavedData.get(world);
      List<InvasionHudRenderInfo> infoList = new ArrayList<>(playersNearby.size());

      for (EntityPlayerMP entityPlayerMP : playersNearby) {
        InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

        if (playerData.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active) {
          continue;
        }

        InvasionPlayerData.InvasionData invasionData = playerData.getInvasionData();

        if (invasionData == null) {
          continue;
        }

        InvasionHudRenderInfo info = new InvasionHudRenderInfo();
        info.playerUuid = entityPlayerMP.getUniqueID();
        info.invasionCompletionPercentage = this.invasionCompletionPercentageCalculator.calculate(playerData.getInvasionData());
        info.invasionName = invasionData.getInvasionName();
        infoList.add(info);
      }

      ModuleOnslaught.PACKET_SERVICE.sendTo(
          new SCPacketHudUpdate(infoList),
          player
      );
    }
  }
}