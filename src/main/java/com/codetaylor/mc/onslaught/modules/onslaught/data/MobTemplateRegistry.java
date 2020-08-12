package com.codetaylor.mc.onslaught.modules.onslaught.data;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Responsible for holding references to {@link MobTemplate}s and providing
 * access to them.
 */
public class MobTemplateRegistry {

  private final Map<String, MobTemplate> templateMap;

  public MobTemplateRegistry(Map<String, MobTemplate> templateMap) {

    this.templateMap = templateMap;
  }

  @Nullable
  public MobTemplate get(String id) {

    return this.templateMap.get(id);
  }
}
