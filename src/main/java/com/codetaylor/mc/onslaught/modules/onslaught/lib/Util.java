package com.codetaylor.mc.onslaught.modules.onslaught.lib;

public final class Util {

  public static int[] evaluateRangeArray(int[] range) {

    int[] result = new int[2];

    if (range.length == 1) {
      result[0] = range[0];
      result[1] = range[0];

    } else if (range.length > 1) {
      result[0] = Math.min(range[0], range[1]);
      result[1] = Math.max(range[0], range[1]);
    }

    return result;
  }

  private Util() {
    //
  }
}
