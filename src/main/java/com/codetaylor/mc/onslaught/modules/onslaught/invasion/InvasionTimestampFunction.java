package com.codetaylor.mc.onslaught.modules.onslaught.invasion;

/**
 * Responsible for calculating an invasion's starting timestamp given the
 * current world time.
 */
public class InvasionTimestampFunction {

  public long apply(long worldTime) {
    /*
        25000 // current time
        25000 + 24000 = 49000
        49000 % 24000 = 1000
        49000 - 1000 = 48000 // start of next day

        invasion starts at 13000
        24000 - 13000 = 11000

        49000 - (1000 + 11000) = 37000
        37000 % 24000 = 13000 // ok
         */
    return (worldTime + 24000) - (((worldTime + 24000) % 24000) + 11000);
  }
}
