package com.codetaylor.mc.onslaught.modules.onslaught.ai;

/**
 * Contains the default priorities for all custom AI tasks.
 */
public final class DefaultPriority {

  // AI Tasks
  public static final int CHASE_LONG_DISTANCE = -10;
  public static final int MINING = -9;
  public static final int ATTACK_MELEE = -3;

  // AI Target Tasks
  public static final int PLAYER_TARGET = 10;

  private DefaultPriority() {
    //
  }
}
