package com.codetaylor.mc.onslaught.modules.onslaught.template.invasion;

import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;

/** Responsible for holding invasion template selector data read from json files. */
public class InvasionTemplateSelector {

  public InvasionTemplateSelectorDimension dimension = new InvasionTemplateSelectorDimension();
  public Stages gamestages = null;
  public int weight = 1;
}
