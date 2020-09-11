package com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector;

import com.codetaylor.mc.athenaeum.util.WeightedPicker;
import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Responsible for selecting an invasion for the given player.
 */
public class InvasionSelector {

  private final Supplier<Stream<Map.Entry<String, InvasionTemplate>>> invasionTemplateStreamSupplier;
  private final Predicate<String> invasionTemplateExistsPredicate;
  private final Supplier<String> fallbackInvasionSupplier;

  public InvasionSelector(
      Supplier<Stream<Map.Entry<String, InvasionTemplate>>> invasionTemplateStreamSupplier,
      Predicate<String> invasionTemplateExistsPredicate,
      Supplier<String> fallbackInvasionSupplier
  ) {

    this.invasionTemplateStreamSupplier = invasionTemplateStreamSupplier;
    this.invasionTemplateExistsPredicate = invasionTemplateExistsPredicate;
    this.fallbackInvasionSupplier = fallbackInvasionSupplier;
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

    // Filter invasions by dimension and gamestage
    // Select remaining invasions by weight

    this.invasionTemplateStreamSupplier.get()
        .filter(new SelectorFilterDimension(new DimensionSupplier(player)))
        .filter(new SelectorFilterGamestages(player))
        .forEach(entry -> picker.add(entry.getValue().selector.weight, entry));

    if (picker.getSize() == 0) {
      // Ensure that the fallback invasion exists else return null
      String fallbackInvasion = this.fallbackInvasionSupplier.get();
      boolean invasionTemplateExists = this.invasionTemplateExistsPredicate.test(fallbackInvasion);

      if (invasionTemplateExists) {
        ModOnslaught.LOG.info(String.format("Selected invasion %s for player %s", fallbackInvasion, player.getName()));
        return fallbackInvasion;

      } else {
        ModOnslaught.LOG.log(Level.SEVERE, String.format("Missing the fallback invasion defined in config, skipping invasion for player %s", player.getName()));
        return null;
      }

    } else {
      String invasion = picker.get().getKey();
      ModOnslaught.LOG.info(String.format("Selected invasion %s for player %s", invasion, player.getName()));
      return invasion;
    }
  }
}