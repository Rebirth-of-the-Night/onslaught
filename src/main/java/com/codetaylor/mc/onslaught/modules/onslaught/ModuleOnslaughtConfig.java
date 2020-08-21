package com.codetaylor.mc.onslaught.modules.onslaught;

import net.minecraftforge.common.config.Config;

@Config(modid = ModuleOnslaught.MOD_ID, name = ModuleOnslaught.MOD_ID + "/" + "onslaught")
public class ModuleOnslaughtConfig {

  public static CustomAI.AttackMelee CUSTOM_AI_ATTACK_MELEE = new CustomAI.AttackMelee();
  public static CustomAI.LongDistanceChase CUSTOM_AI_LONG_DISTANCE_CHASE = new CustomAI.LongDistanceChase();
  public static CustomAI.Mining CUSTOM_AI_MINING = new CustomAI.Mining();

  public static class CustomAI {

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

    public static class LongDistanceChase {

      @Config.Comment({
          "The default long distance chase speed.",
          "Can be overridden in a mob template or with NBT.",
          "Default: " + 1
      })
      public double DEFAULT_SPEED = 1;
    }

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
