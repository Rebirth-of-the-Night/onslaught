package com.codetaylor.mc.onslaught.modules.onslaught.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import javax.annotation.Nullable;

public interface IInvasionPlayerData {

  enum EnumInvasionState {

    /**
     * Player's timer is still ticking down.
     */
    Waiting(0),

    /**
     * Player's timer has expired and they have been flagged as eligible for
     * an invasion.
     */
    Eligible(1),

    /**
     * Player has been selected from the collection of eligible players and their
     * invasion data has been assigned.
     */
    Pending(2),

    /**
     * Player's invasion has begun and waves are spawning.
     */
    Active(3);

    private static final Int2ObjectMap<EnumInvasionState> MAP;

    static {
      EnumInvasionState[] states = EnumInvasionState.values();
      MAP = new Int2ObjectOpenHashMap<>(states.length);

      for (EnumInvasionState state : states) {
        MAP.put(state.id, state);
      }
    }

    private final int id;

    EnumInvasionState(int id) {

      this.id = id;
    }

    public int getId() {

      return this.id;
    }

    public static EnumInvasionState from(int id) {

      if (!MAP.containsKey(id)) {
        throw new IllegalArgumentException("Unknown id for state: " + id);
      }

      return MAP.get(id);
    }
  }

  int getTicksUntilEligible();

  void setTicksUntilEligible(int ticksUntilEligible);

  EnumInvasionState getInvasionState();

  void setInvasionState(EnumInvasionState invasionState);

  @Nullable
  InvasionPlayerData.InvasionData getInvasionData();

  void setInvasionData(@Nullable InvasionPlayerData.InvasionData invasionData);

  boolean hasInvasionData();
}
