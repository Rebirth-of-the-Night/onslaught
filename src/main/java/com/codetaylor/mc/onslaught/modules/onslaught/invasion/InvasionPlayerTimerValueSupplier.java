package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

import com.codetaylor.mc.athenaeum.util.RandomHelper;

import java.util.function.IntSupplier;

public class InvasionPlayerTimerValueSupplier
    implements IntSupplier {

  private final IntSupplier invasionTimingRangeMin;
  private final IntSupplier invasionTimingRangeMax;

  public InvasionPlayerTimerValueSupplier(IntSupplier invasionTimingRangeMin, IntSupplier invasionTimingRangeMax) {

    this.invasionTimingRangeMin = invasionTimingRangeMin;
    this.invasionTimingRangeMax = invasionTimingRangeMax;
  }

  @Override
  public int getAsInt() {

    int min = this.invasionTimingRangeMin.getAsInt();
    int max = this.invasionTimingRangeMax.getAsInt();
    min = Math.min(max, min);
    max = Math.max(max, min);
    return RandomHelper.random().nextInt(max - min + 1) + min;
  }
}
