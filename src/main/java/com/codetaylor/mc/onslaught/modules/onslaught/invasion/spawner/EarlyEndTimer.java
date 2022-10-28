package com.codetaylor.mc.onslaught.modules.onslaught.invasion.spawner;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.event.InvasionStateChangedEvent;
import com.codetaylor.mc.onslaught.modules.onslaught.event.handler.InvasionUpdateEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionGlobalSavedData;
import com.codetaylor.mc.onslaught.modules.onslaught.invasion.InvasionPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.IntSupplier;

/**
 * Responsible for updating the wave delay timers for all active invasions.
 */
public class EarlyEndTimer implements InvasionUpdateEventHandler.IInvasionUpdateComponent {

	private final IntSupplier invasionPlayerTimerValueSupplier;

	public EarlyEndTimer(IntSupplier invasionPlayerTimerValueSupplier)
	{this.invasionPlayerTimerValueSupplier = invasionPlayerTimerValueSupplier;}

	@Override
	public void update(int updateIntervalTicks, InvasionGlobalSavedData invasionGlobalSavedData, PlayerList playerList, long worldTime) {

		for (EntityPlayerMP player : playerList.getPlayers()) {

			InvasionPlayerData data = invasionGlobalSavedData.getPlayerData(player.getUniqueID());

			if (data.getInvasionState() != InvasionPlayerData.EnumInvasionState.Active) {
				continue;
			}

			InvasionPlayerData.InvasionData invasionData = data.getInvasionData();

			if (invasionData == null) {
				continue;
			}

			if (invasionData.getTimeToEnd() >= 1) {
				invasionData.setTimeToEnd(invasionData.getTimeToEnd() - 1);
			}

			if (invasionData.getEarlyEndMessage() != null && !invasionData.getEarlyEndMessage().isEmpty()) {
				TextComponentTranslation textComponentTranslation =
						new TextComponentTranslation(invasionData.getEarlyEndMessage());
				player.sendMessage(textComponentTranslation);
			}

			if (invasionData.getTimeToEnd() == 0) {
				invasionData.setTimeToEnd(-1);
				MinecraftForge.EVENT_BUS.post(
						new InvasionStateChangedEvent(player, InvasionPlayerData.EnumInvasionState.Active, InvasionPlayerData.EnumInvasionState.Waiting)
				);

				data.setTicksUntilEligible(this.invasionPlayerTimerValueSupplier.getAsInt());
				data.setInvasionState(InvasionPlayerData.EnumInvasionState.Waiting);
				data.setInvasionData(null);
				invasionGlobalSavedData.markDirty();

				if (ModuleOnslaughtConfig.DEBUG.INVASION_STATE) {
					String message =
							String.format("Set invasion state to %s for player %s", "Waiting", player.getName());
					ModOnslaught.LOG.fine(message);
					System.out.println(message);
				}
			}
		}
	}

}
