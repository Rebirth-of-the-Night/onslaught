package com.codetaylor.mc.onslaught.modules.onslaught.lib;

public class TickIntervalCounter {

  /**
   * Stores the previous world time, or -1 if first tick.
   */
  private long previousWorldTime;

  public TickIntervalCounter() {

    this.previousWorldTime = -1;
  }

  /**
   * This tracks the previous world time and uses it to derive the number of
   * actual ticks passed. This will account for any changes made to world time
   * via the /time add X command and sleeping.
   *
   * @param updateIntervalTicks the update interval in ticks
   * @param worldTime           the current world time
   * @return the number of actual interval ticks
   */
  public int updateAndGet(int updateIntervalTicks, long worldTime) {

    int updateTicks;

    if (this.previousWorldTime == -1) {
      updateTicks = updateIntervalTicks;

    } else {
      int amount = (int) (worldTime - this.previousWorldTime);

      if (amount < 0) {
        amount = 0;
      }

      updateTicks = amount;
    }

    this.previousWorldTime = worldTime;
    return updateTicks;
  }
}