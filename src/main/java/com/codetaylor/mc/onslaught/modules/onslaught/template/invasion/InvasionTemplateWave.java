package com.codetaylor.mc.onslaught.modules.onslaught.template.invasion;

import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaughtConfig;

/** Responsible for holding invasion template wave data read from json files. */
public class InvasionTemplateWave {

  /**
   * The delay from the start of the invasion before this wave is spawned. Can be specified as a
   * single, fixed value or two min/max values.
   */
  public int[] delayTicks = {0};

  /** The wave's groups. One will be selected. */
  public Group[] groups = {};

  /** The fallback mob used if the first spawn fails. */
  public SecondaryMob secondaryMob = new SecondaryMob();

  public enum EnumSpawnType {

    /*
     * These are lowercase for consistency between the json templates, the
     * default config values, and the serialized player data NBT.
     */

    ground,
    air
  }

  public static class Group {

    /** The group's weight. Defaults to 1. */
    public int weight = 1;

    /** Try to force spawn the primary spawn. */
    public boolean forceSpawn = ModuleOnslaughtConfig.WAVE.DEFAULT_FORCE_SPAWN;

    /** The group's mobs. All mobs in this list will be spawned. */
    public Mob[] mobs = {};
  }

  public static class Mob {

    /** The mob template id. Required. */
    public String id;

    /**
     * The number of mobs to spawn from this template. Can be specified as a single, fixed value or
     * two min/max values.
     */
    public int[] count = {1};

    /** The spawn parameters. */
    public Spawn spawn = new Spawn();
  }

  public static class SecondaryMob {

    /** The mob template id. Required. */
    public String id = ModuleOnslaughtConfig.WAVE.DEFAULT_SECONDARY_MOB_ID;

    /** The spawn parameters. */
    public SecondarySpawn spawn = new SecondarySpawn();
  }

  public static class Spawn {

    public EnumSpawnType type;
    public int[] light;
    public int[] rangeXZ;
    public int rangeY;
    public int stepRadius;
    public int sampleDistance;

    public Spawn() {

      this.type = ModuleOnslaughtConfig.WAVE.DEFAULT_SPAWN.DEFAULT_TYPE;
      this.light = ModuleOnslaughtConfig.WAVE.DEFAULT_SPAWN.DEFAULT_LIGHT;
      this.rangeXZ = ModuleOnslaughtConfig.WAVE.DEFAULT_SPAWN.DEFAULT_RANGE_XZ;
      this.rangeY = ModuleOnslaughtConfig.WAVE.DEFAULT_SPAWN.DEFAULT_RANGE_Y;
      this.stepRadius = ModuleOnslaughtConfig.WAVE.DEFAULT_SPAWN.DEFAULT_STEP_RADIUS;
      this.sampleDistance = ModuleOnslaughtConfig.WAVE.DEFAULT_SPAWN.DEFAULT_SAMPLE_DISTANCE;
    }
  }

  public static class SecondarySpawn extends Spawn {

    public SecondarySpawn() {

      this.type = ModuleOnslaughtConfig.WAVE.DEFAULT_SECONDARY_SPAWN.DEFAULT_TYPE;
      this.light = ModuleOnslaughtConfig.WAVE.DEFAULT_SECONDARY_SPAWN.DEFAULT_LIGHT;
      this.rangeXZ = ModuleOnslaughtConfig.WAVE.DEFAULT_SECONDARY_SPAWN.DEFAULT_RANGE_XZ;
      this.rangeY = ModuleOnslaughtConfig.WAVE.DEFAULT_SECONDARY_SPAWN.DEFAULT_RANGE_Y;
      this.stepRadius = ModuleOnslaughtConfig.WAVE.DEFAULT_SECONDARY_SPAWN.DEFAULT_STEP_RADIUS;
      this.sampleDistance =
          ModuleOnslaughtConfig.WAVE.DEFAULT_SECONDARY_SPAWN.DEFAULT_SAMPLE_DISTANCE;
    }
  }
}
