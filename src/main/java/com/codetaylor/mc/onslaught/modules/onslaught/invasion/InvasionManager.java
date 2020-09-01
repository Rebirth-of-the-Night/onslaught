package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.RandomHelper;
import com.codetaylor.mc.athenaeum.util.WeightedPicker;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.CapabilityInvasion;
import com.codetaylor.mc.onslaught.modules.onslaught.capability.IInvasionPlayerData;
import com.codetaylor.mc.onslaught.modules.onslaught.data.DataStore;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * Responsible for starting, stopping, and tracking invasions.
 */
public class InvasionManager {

  private final DataStore dataStore;
  private final EligiblePlayerQueue eligiblePlayerQueue;

  public InvasionManager(DataStore dataStore, EligiblePlayerQueue eligiblePlayerQueue) {

    this.dataStore = dataStore;
    this.eligiblePlayerQueue = eligiblePlayerQueue;
  }

  public void notifyPlayerEligibleForInvasion(EntityPlayer player) {

    UUID uuid = player.getUniqueID();
    this.eligiblePlayerQueue.add(uuid);
  }

  public void stopInvasionForPlayer(EntityPlayer player) {

    UUID uuid = player.getUniqueID();
    this.eligiblePlayerQueue.remove(uuid);
  }

  public void resetInvasionTimerForPlayer(EntityPlayer player) {

    IInvasionPlayerData data = CapabilityInvasion.get(player);
    int min = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[0];
    int max = ModuleOnslaughtConfig.INVASION.TIMING_RANGE_TICKS[1];
    data.setTicksUntilNextInvasion(RandomHelper.random().nextInt(max - min) + min);
  }

  @Nullable
  private String selectInvasionForPlayer(EntityPlayer player) {

    final WeightedPicker<Map.Entry<String, InvasionTemplate>> picker = new WeightedPicker<>(player.getRNG());
    InvasionTemplateRegistry invasionTemplateRegistry = this.dataStore.getInvasionTemplateRegistry();

    // Filter invasions by dimension and gamestage
    // Select remaining invasions by weight

    invasionTemplateRegistry.getAll().stream()
        .filter(new InvasionFilterDimension(new DimensionSupplier(player)))
        .filter(new InvasionFilterGamestages(player))
        .forEach(entry -> {
          picker.add(entry.getValue().selector.weight, entry);
        });

    if (picker.getSize() == 0) {
      // Ensure that the fallback invasion exists else return null
      String fallbackInvasion = ModuleOnslaughtConfig.INVASION.DEFAULT_FALLBACK_INVASION;
      InvasionTemplate invasionTemplate = invasionTemplateRegistry.get(fallbackInvasion);
      return (invasionTemplate == null) ? null : fallbackInvasion;

    } else {
      return picker.get().getKey();
    }
  }

  public void update(WorldServer world) {

  }

}
