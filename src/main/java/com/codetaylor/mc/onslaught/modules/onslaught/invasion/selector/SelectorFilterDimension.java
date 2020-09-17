package com.codetaylor.mc.onslaught.modules.onslaught.invasion.selector;

import com.codetaylor.mc.athenaeum.util.ArrayHelper;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateSelectorDimension;

import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * Responsible for filtering out invasion templates that do not match the
 * dimension id returned by the given dimension id supplier.
 */
public class SelectorFilterDimension
    implements Predicate<Map.Entry<String, InvasionTemplate>> {

  private final IntSupplier dimensionSupplier;

  public SelectorFilterDimension(IntSupplier dimensionSupplier) {

    this.dimensionSupplier = dimensionSupplier;
  }

  @Override
  public boolean test(Map.Entry<String, InvasionTemplate> entry) {

    InvasionTemplate template = entry.getValue();
    InvasionTemplateSelectorDimension.Type type = template.selector.dimension.type;
    int[] dimensions = template.selector.dimension.dimensions;
    int dimension = this.dimensionSupplier.getAsInt();

    switch (type) {
      case INCLUDE:
        return ArrayHelper.containsInt(dimensions, dimension);
      case EXCLUDE:
        return !ArrayHelper.containsInt(dimensions, dimension);
    }

    return false;
  }
}
