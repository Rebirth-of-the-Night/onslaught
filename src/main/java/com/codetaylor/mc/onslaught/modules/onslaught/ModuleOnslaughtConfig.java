package com.codetaylor.mc.onslaught.modules.onslaught;

import net.minecraftforge.common.config.Config;

@Config(modid = ModuleOnslaught.MOD_ID, name = ModuleOnslaught.MOD_ID + "/" + "onslaught")
public class ModuleOnslaughtConfig {

  public static CustomAI.LongDistanceChase CUSTOM_AI_LONG_DISTANCE_CHASE = new CustomAI.LongDistanceChase();
  public static CustomAI.Mining CUSTOM_AI_MINING = new CustomAI.Mining();

  public static class CustomAI {

    public static class LongDistanceChase {

      @Config.Comment({
          "The default long distance chase speed. Can be overridden in a mob template.",
          "Default: " + 1
      })
      public double DEFAULT_SPEED = 1;
    }

    public static class Mining {

      @Config.Comment({
          "The default mining range used. Can be overridden in a mob template.",
          "Default: " + 4
      })
      public int DEFAULT_RANGE = 4;

      @Config.Comment({
          "The default mining speed modifier used. Can be overridden in a mob template.",
          "Default: " + 1
      })
      public double DEFAULT_SPEED_MODIFIER = 1;
    }
  }

}
