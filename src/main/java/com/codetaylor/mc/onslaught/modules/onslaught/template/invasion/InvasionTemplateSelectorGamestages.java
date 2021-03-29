package com.codetaylor.mc.onslaught.modules.onslaught.template.invasion;

import com.google.gson.annotations.SerializedName;

/** Responsible for holding invasion template selector gamestage data read from json files. */
public class InvasionTemplateSelectorGamestages {

  public Type type = Type.ALL;
  public String[] stages = {};
  public String[] not = {};

  public enum Type {
    @SerializedName("all")
    ALL,

    @SerializedName("one")
    ONE
  }
}
