package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.ArrayHelper;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateSelectorDimension;

import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public class InvasionFilterDimension
    implements Predicate<Map.Entry<String, InvasionTemplate>> {

  private final IntSupplier dimensionSupplier;

  public InvasionFilterDimension(IntSupplier dimensionSupplier) {

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
