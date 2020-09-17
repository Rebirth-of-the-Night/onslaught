package com.codetaylor.mc.onslaught.modules.onslaught.template.invasion;

import com.google.gson.annotations.SerializedName;

/**
 * Responsible for holding invasion template selector dimension data read from json files.
 */
public class InvasionTemplateSelectorDimension {

  public Type type = Type.EXCLUDE;
  public int[] dimensions = {};

  public enum Type {
    @SerializedName("include")
    INCLUDE,

    @SerializedName("exclude")
    EXCLUDE
  }
}
