package com.codetaylor.mc.onslaught.modules.onslaught;

import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateWave;
import net.minecraftforge.common.config.Config;

@Config(modid = ModuleOnslaught.MOD_ID)
public class ModuleOnslaughtConfig {

  public static Debug DEBUG = new Debug();
  public static Client CLIENT = new Client();
  public static Invasion INVASION = new Invasion();
  public static Wave WAVE = new Wave();
  public static CustomAI CUSTOM_AI = new CustomAI();

  public static class Debug {

    public boolean INVASION_STATE = false;
    public boolean INVASION_SPAWNERS = false;
    public boolean INVASION_SPAWN_SAMPLER = false;
    public boolean INVASION_DATA_UPDATES = false;
    public boolean INVASION_SELECTOR = false;
  }

  public static class Client {

    @Config.Comment({"XY position of the invasion HUD.", "Default: [16, 16]"})
    public int[] INVASION_HUD_POSITION_XY = new int[] {16, 16};

    @Config.Comment({"Width of the invasion HUD progress bars.", "Default: " + 128})
    public int INVASION_HUD_BAR_WIDTH = 128;

    @Config.Comment({"RGB color of the invasion HUD progress bars.", "Default: [0, 0, 255]"})
    @Config.RangeInt(min = 0, max = 255)
    public int[] INVASION_HUD_BAR_COLOR_RGB = new int[] {0, 0, 255};

    @Config.Comment({
      "Displays active invasions of player within this block range.",
      "A chunk is 16 blocks.",
      "Default: " + (16 * 8)
    })
    public int INVASION_HUD_UPDATE_RANGE = 16 * 8;
  }

  public static class Invasion {

    @Config.Comment({
      "The min and max number of ticks before a player becomes eligible for an invasion.",
      "For reference, a Minecraft day is 24000 ticks long.",
      "Default: [120000, 168000] or 5-7 Minecraft days"
    })
    public int[] TIMING_RANGE_TICKS = {5 * 24000, 7 * 24000};

    @Config.Comment({
      "The maximum number of invasions that can occur at the same time.",
      "Default: " + 5
    })
    public int MAX_CONCURRENT_INVASIONS = 5;

    @Config.Comment({
      "This is the name of the default invasion that will be selected if no",
      "invasion qualifies for a player. This invasion's selection qualifiers",
      "will be ignored. If this is blank and no invasion qualifies for a player,",
      "no invasion will occur for said player.",
      "Default: " + ""
    })
    public String DEFAULT_FALLBACK_INVASION = "";

    @Config.Comment({"The number of ticks to delay the forced spawns.", "Default: " + (10 * 20)})
    public int FORCED_SPAWN_DELAY_TICKS = 10 * 20;

    @Config.Comment({
      "Effect id's of effects to be applied when a player is near a forced",
      "spawn location. These effects will be continuously applied while a",
      "player is within range."
    })
    @Config.RequiresMcRestart
    public String[] FORCED_SPAWN_EFFECTS = {"minecraft:slowness"};

    @Config.Comment({
      "If a player is within this range of a forced spawn location, the forced",
      "spawn effects will be applied.",
      "Default: " + 16
    })
    public int FORCED_SPAWN_EFFECT_RANGE = 16;

    @Config.Comment({
      "Duration of effects applied when a player is within range of a forced",
      "spawn location.",
      "Default: " + (10 * 20)
    })
    public int FORCED_SPAWN_EFFECT_DURATION_TICKS = 10 * 20;

    @Config.Comment({
      "Duration in ticks that a player can be offline before their invasion",
      "mobs are stripped of invasion data.",
      "Default: " + (2 * 60 * 20)
    })
    public int OFFLINE_CLEANUP_DELAY_TICKS = 2 * 60 * 20;

    @Config.Comment({
      "The number of ticks prior to the invasion that the warning message",
      "will be sent.",
      "Set to -1 to disable.",
      "Default: " + (-1)
    })
    public int DEFAULT_MESSAGE_WARNING_TICKS = -1;

    @Config.Comment({
      "The default warning message sent to the player.",
      "Can be a string or lang key.",
      "Default: " + "message.onslaught.invasion.warn"
    })
    public String DEFAULT_MESSAGE_WARNING = "message.onslaught.invasion.warn";

    @Config.Comment({
      "The default invasion start message sent to the player.",
      "Can be a string or lang key.",
      "Set to an empty string to disable.",
      "Default: " + "message.onslaught.invasion.begin"
    })
    public String DEFAULT_MESSAGE_BEGIN = "message.onslaught.invasion.begin";

    @Config.Comment({
      "The default invasion end message sent to the player.",
      "Can be a string or lang key.",
      "Set to an empty string to disable.",
      "Default: " + "message.onslaught.invasion.end"
    })
    public String DEFAULT_MESSAGE_END = "message.onslaught.invasion.end";
  }

  public static class Wave {

    public Spawn DEFAULT_SPAWN = new Spawn();

    public String DEFAULT_SECONDARY_MOB_ID = "";

    public boolean DEFAULT_FORCE_SPAWN = true;

    public Spawn DEFAULT_SECONDARY_SPAWN =
        new Spawn(
            InvasionTemplateWave.EnumSpawnType.air,
            new int[] {0, 15},
            new int[] {16, 128},
            16,
            4,
            2);
  }

  public static class Spawn {

    @Config.Comment({"The default spawn type."})
    public InvasionTemplateWave.EnumSpawnType DEFAULT_TYPE =
        InvasionTemplateWave.EnumSpawnType.ground;

    @Config.Comment({"The default light range."})
    @Config.RangeInt(min = 0, max = 15)
    public int[] DEFAULT_LIGHT = {0, 7};

    @Config.Comment({"The default minimum and maximum spawn range."})
    @Config.RangeInt(min = 0)
    public int[] DEFAULT_RANGE_XZ = {16, 128};

    @Config.Comment({"The default vertical spawn range."})
    @Config.RangeInt(min = 0)
    public int DEFAULT_RANGE_Y = 16;

    @Config.Comment({"The default spawn range step radius."})
    @Config.RangeInt(min = 1)
    public int DEFAULT_STEP_RADIUS = 4;

    @Config.Comment({"The default spawn sample distance."})
    @Config.RangeInt(min = 1)
    public int DEFAULT_SAMPLE_DISTANCE = 2;

    public Spawn() {
      //
    }

    public Spawn(
        InvasionTemplateWave.EnumSpawnType DEFAULT_TYPE,
        int[] DEFAULT_LIGHT,
        int[] DEFAULT_RANGE_XZ,
        int DEFAULT_RANGE_Y,
        int DEFAULT_STEP_RADIUS,
        int DEFAULT_SAMPLE_DISTANCE) {

      this.DEFAULT_TYPE = DEFAULT_TYPE;
      this.DEFAULT_LIGHT = DEFAULT_LIGHT;
      this.DEFAULT_RANGE_XZ = DEFAULT_RANGE_XZ;
      this.DEFAULT_RANGE_Y = DEFAULT_RANGE_Y;
      this.DEFAULT_STEP_RADIUS = DEFAULT_STEP_RADIUS;
      this.DEFAULT_SAMPLE_DISTANCE = DEFAULT_SAMPLE_DISTANCE;
    }
  }

  public static class CustomAI {

    public AntiAir ANTI_AIR = new AntiAir();
    public Lunge LUNGE = new Lunge();
    public ExplodeWhenStuck EXPLODE_WHEN_STUCK = new ExplodeWhenStuck();
    public CounterAttack COUNTER_ATTACK = new CounterAttack();
    public AttackMelee ATTACK_MELEE = new AttackMelee();
    public LongDistanceChase LONG_DISTANCE_CHASE = new LongDistanceChase();
    public Mining MINING = new Mining();
    @Config.Name("Offscreen Teleport")
    public OffscreenTeleport OFFSCREEN_TELEPORT = new OffscreenTeleport();

    public static class AntiAir {

      @Config.Comment({
        "The default range at which a mob will pull its target down.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 128
      })
      @Config.RangeInt(min = 1)
      public int DEFAULT_RANGE = 128;

      @Config.Comment({
        "Set to true to require line of sight with target.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + true
      })
      public boolean DEFAULT_SIGHT_REQUIRED = true;

      @Config.Comment({
        "The default Y motion value applied to the mob's target.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + (-0.4)
      })
      @Config.RangeInt(max = 0)
      public double DEFAULT_MOTION_Y = -0.4;

      @Config.Comment({
        "The number of ticks that the player is allowed to be in the air",
        "before they are pulled down.",
        "Default: " + 15
      })
      @Config.RangeInt(min = 0)
      public int DELAY_TICKS = 15;

      @Config.Comment({
        "If true, the force applied from all nearby AntiAir mobs will be summed",
        "before application. If false, only the greatest force from all nearby",
        "AntiAir mobs will be applied.",
        "Default: " + false
      })
      public boolean CUMULATIVE_MOTION_Y = false;
    }

    public static class Lunge {

      @Config.Comment({
        "The default range at which a mob will increase its speed.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 6
      })
      @Config.RangeInt(min = 1)
      public int DEFAULT_RANGE = 6;

      @Config.Comment({
        "The default speed modifier used.",
        "For reference, the sprint speed modifier is 0.3.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 0.3
      })
      @Config.RangeDouble(min = 0)
      public double DEFAULT_SPEED_MODIFIER = 0.3;
    }

    public static class ExplodeWhenStuck {

      @Config.Comment({
        "Set to true to require line of sight with target.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + false
      })
      public boolean DEFAULT_SIGHT_REQUIRED = false;

      @Config.Comment({
        "Set to false to ignore the range to target.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + true
      })
      public boolean DEFAULT_RANGE_REQUIRED = true;

      @Config.Comment({
        "The default target range to allow the mob to explode.",
        "Can be overridden in a mob template or with NBT.",
        "Default: [2, 16]"
      })
      public double[] DEFAULT_RANGE = {2, 16};

      @Config.Comment({
        "The default explosion delay.",
        "How long the mob can be stuck before it explodes.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + (3 * 20)
      })
      @Config.RangeInt(min = 0)
      public int DEFAULT_EXPLOSION_DELAY_TICKS = 3 * 20;

      @Config.Comment({
        "The default explosion strength.",
        "For reference, an uncharged creeper has an explosion strength of 3",
        "and a charged creeper has an explosion strength of 6.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 3
      })
      @Config.RangeInt(min = 1)
      public double DEFAULT_EXPLOSION_STRENGTH = 3;

      @Config.Comment({
        "Set to true to cause the explosion to start fires.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + false
      })
      public boolean DEFAULT_EXPLOSION_CAUSES_FIRE = false;

      @Config.Comment({
        "Set to false to cause the explosion to be harmless.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + true
      })
      public boolean DEFAULT_EXPLOSION_DAMAGING = true;
    }

    public static class CounterAttack {

      @Config.Comment({
        "The default Y motion value when counter attacking.",
        "For reference the spider's Y leap motion value is 0.4",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 0.4
      })
      @Config.RangeDouble(min = 0)
      public double DEFAULT_LEAP_MOTION_Y = 0.4;

      @Config.Comment({
        "The default XZ motion value when counter attacking.",
        "For reference the spider's XZ leap motion value is 0.4",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 0.4
      })
      @Config.RangeDouble(min = 0)
      public double DEFAULT_LEAP_MOTION_XZ = 0.4;

      @Config.Comment({
        "The default chance of counter attacking.",
        "For reference, the spider's leap chance is 0.25.",
        "Note: The chance will not prevent the task from executing,",
        "but it may delay, or at least vary, the timing of the execution.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 0.25
      })
      @Config.RangeDouble(min = 0, max = 1)
      public double DEFAULT_CHANCE = 0.25;

      @Config.Comment({
        "The default range for allowing a counter attack.",
        "For reference the spider's leap range is [2,4].",
        "Can be overridden in a mob template or with NBT.",
        "Default: [2, 4]"
      })
      public double[] DEFAULT_RANGE = {2, 4};
    }

    public static class AttackMelee {

      @Config.Comment({
        "The default movement speed when attacking.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 1
      })
      @Config.RangeDouble(min = 0)
      public double DEFAULT_SPEED = 1;

      @Config.Comment({
        "The default attack damage.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 1
      })
      @Config.RangeDouble(min = 0)
      public double DEFAULT_ATTACK_DAMAGE = 1;
    }

    public static class LongDistanceChase {

      @Config.Comment({
        "The default long distance chase speed.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 1
      })
      @Config.RangeDouble(min = 0)
      public double DEFAULT_SPEED = 1;
    }

    public static class Mining {

      @Config.Comment({
        "The default mining range used.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 4
      })
      @Config.RangeInt(min = 1)
      public int DEFAULT_RANGE = 4;

      @Config.Comment({
        "The default mining speed modifier used.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 1
      })
      @Config.RangeDouble(min = 0)
      public double DEFAULT_SPEED_MODIFIER = 1;
    }

    public static class OffscreenTeleport {
      @Config.Comment({
        "The default offscreen teleport range for mobs to trigger a relocate.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 64
      })
      @Config.RangeInt(min = 1)
      public int DEFAULT_RANGE = 64;

      @Config.Comment({
        "The default teleportation factor to 'move closer' to the target.",
        "0.5 => move half the distance",
        "1.0 => move directly on top of target",
        "1.5 => move to the target, then half again to flank the target.",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + 1.5
      })
      @Config.RangeDouble(min = 0, max = 2)
      public double DEFAULT_FACTOR = 1.5;

      @Config.Comment({
        "The default ability to teleport across dimensions to chase the target",
        "Can be overridden in a mob template or with NBT.",
        "Default: " + true
      })
      public boolean DEFAULT_DIM_HOPPING = true;
    }
  }
}
