package com.codetaylor.mc.onslaught.modules.onslaught;

import net.minecraftforge.common.config.Config;

@Config(modid = ModuleOnslaught.MOD_ID, name = ModuleOnslaught.MOD_ID + "/" + "onslaught")
public class ModuleOnslaughtConfig {

  public static CustomAI CUSTOM_AI = new CustomAI();

  public static class CustomAI {

    public CounterAttack COUNTER_ATTACK = new CounterAttack();

    public static class CounterAttack {

      @Config.Comment({
          "The default Y motion value when counter attacking.",
          "For reference the spider's Y leap motion value is 0.4",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 0.4
      })
      public double DEFAULT_LEAP_MOTION_Y = 0.4;

      @Config.Comment({
          "The default XZ motion value when counter attacking.",
          "For reference the spider's XZ leap motion value is 0.4",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 0.4
      })
      public double DEFAULT_LEAP_MOTION_XZ = 0.4;

      @Config.Comment({
          "The default chance of counter attacking.",
          "For reference, the spider's leap chance is 0.25.",
          "Note: The chance will not prevent the task from executing,",
          "but it may delay, or at least vary, the timing of the execution.",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 0.25
      })
      public double DEFAULT_CHANCE = 0.25;

      @Config.Comment({
          "The default range for allowing a counter attack.",
          "For reference the spider's leap range is [2,4].",
          "Can be overridden in a mob template or with NBT.",
          "Default: [2, 4]"
      })
      public double[] DEFAULT_RANGE = {2, 4};
    }

    public AttackMelee ATTACK_MELEE = new AttackMelee();

    public static class AttackMelee {

      @Config.Comment({
          "The default movement speed when attacking.",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 1
      })
      public double DEFAULT_SPEED = 1;

      @Config.Comment({
          "The default attack damage.",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 1
      })
      public double DEFAULT_ATTACK_DAMAGE = 1;
    }

    public LongDistanceChase LONG_DISTANCE_CHASE = new LongDistanceChase();

    public static class LongDistanceChase {

      @Config.Comment({
          "The default long distance chase speed.",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 1
      })
      public double DEFAULT_SPEED = 1;
    }

    public Mining MINING = new Mining();

    public static class Mining {

      @Config.Comment({
          "The default mining range used.",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 4
      })
      public int DEFAULT_RANGE = 4;

      @Config.Comment({
          "The default mining speed modifier used.",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 1
      })
      public double DEFAULT_SPEED_MODIFIER = 1;
    }
  }

}
