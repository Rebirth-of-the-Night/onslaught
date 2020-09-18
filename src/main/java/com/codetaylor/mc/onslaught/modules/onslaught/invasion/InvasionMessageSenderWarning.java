package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionUpdateEventHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;

/**
 * Responsible for periodically checking all players and sending a warning message
 * when applicable.
 */
public class InvasionMessageSenderWarning
    implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

  private final InvasionMessageSender invasionMessageSender;

  public InvasionMessageSenderWarning(InvasionMessageSender invasionMessageSender) {

    this.invasionMessageSender = invasionMessageSender;
  }

  @Override
  public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

    for (EntityPlayerMP player : playerList.getPlayers()) {
      InvasionPlayerData playerData = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

      if (playerData.getInvasionState() != InvasionPlayerData.EnumInvasionState.Pending) {
        continue;
      }

      InvasionPlayerData.InvasionData invasionData = playerData.getInvasionData();

      if (invasionData == null) {
        continue;
      }

      long warningMessageTimestamp = invasionData.getWarningMessageTimestamp();

      if (warningMessageTimestamp < 0) {
        continue;
      }

      if (warningMessageTimestamp < worldTime) {
        this.invasionMessageSender.sendMessage(player);
        invasionData.setWarningMessageTimestamp(-1);
        invasionGlobalSavedData.markDirty();
      }
    }
  }
}