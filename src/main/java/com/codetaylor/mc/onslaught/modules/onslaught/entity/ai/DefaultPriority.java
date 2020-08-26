package com.codetaylor.mc.onslaught.modules.onslaught.entity.ai;

/**
 * Contains the default priorities for all custom AI tasks.
 */
public final class DefaultPriority {

  // AI Tasks
  public static final int LUNGE = -15;
  public static final int CHASE_LONG_DISTANCE = -10;
  public static final int MINING = -9;
  public static final int EXPLODE_WHEN_STUCK = -5;
  public static final int ANTI_AIR = -4;
  public static final int ATTACK_MELEE = -3;
  public static final int COUNTER_ATTACK = -2;

  // AI Target Tasks
  public static final int PLAYER_TARGET = 10;

  private DefaultPriority() {
    //
  }
}
