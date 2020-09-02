package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.WeightedPicker;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateRegistry;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Responsible for selecting an invasion for the given player.
 */
public class InvasionSelector {

  private final Supplier<InvasionTemplateRegistry> templateRegistrySupplier;

  public InvasionSelector(Supplier<InvasionTemplateRegistry> templateRegistrySupplier) {

    this.templateRegistrySupplier = templateRegistrySupplier;
  }

  /**
   * Filter invasions by dimension and gamestage then select a qualified
   * invasion by weight.
   *
   * @param player the player to select for
   * @return the invasion id
   */
  @Nullable
  public String selectInvasionForPlayer(EntityPlayer player) {

    final WeightedPicker<Map.Entry<String, InvasionTemplate>> picker = new WeightedPicker<>(player.getRNG());
    InvasionTemplateRegistry invasionTemplateRegistry = this.templateRegistrySupplier.get();

    // Filter invasions by dimension and gamestage
    // Select remaining invasions by weight

    invasionTemplateRegistry.getAll().stream()
        .filter(new InvasionFilterDimension(new DimensionSupplier(player)))
        .filter(new InvasionFilterGamestages(player))
        .forEach(entry -> picker.add(entry.getValue().selector.weight, entry));

    if (picker.getSize() == 0) {
      // Ensure that the fallback invasion exists else return null
      String fallbackInvasion = ModuleOnslaughtConfig.INVASION.DEFAULT_FALLBACK_INVASION;
      return (invasionTemplateRegistry.has(fallbackInvasion)) ? fallbackInvasion : null;

    } else {
      return picker.get().getKey();
    }
  }

}
