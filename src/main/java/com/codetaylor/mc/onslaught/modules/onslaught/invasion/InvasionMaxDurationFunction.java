package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

/** Responsible for calculating an invasion's starting timestamp given the current world time. */
public class InvasionMaxDurationFunction {

  public long apply(long worldTime) {
    /* next afternoon */
    long ticksOfCurrentDay = (worldTime % 24000);
    return (worldTime - ticksOfCurrentDay) + 13000;
  }
}
