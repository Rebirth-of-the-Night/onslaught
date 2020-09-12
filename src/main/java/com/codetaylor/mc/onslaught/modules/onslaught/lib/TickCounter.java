package com.codetaylor.mc.onslaught.modules.onslaught.lib;

/**
 * Responsible for counting ticks.
 */
public class TickCounter {

  private final int max;
  private int count;

  public TickCounter(int max) {

    this(max, 0);
  }

  public TickCounter(int max, int count) {

    this.max = max;
    this.count = count;
  }

  public void reset() {

    this.count = 0;
  }

  public boolean increment(int ticks) {

    this.count += ticks;

    if (this.count >= this.max) {
      this.reset();
      return true;
    }

    return false;
  }
}
