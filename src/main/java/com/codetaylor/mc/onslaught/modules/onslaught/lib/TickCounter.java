package com.codetaylor.mc.onslaught.modules.onslaught.lib;

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

  public boolean increment() {

    return this.increment(1);
  }

  public boolean increment(int ticks) {

    this.count += ticks;

    if (this.count >= this.max) {
      this.count -= this.max;
      return true;
    }

    return false;
  }
}
