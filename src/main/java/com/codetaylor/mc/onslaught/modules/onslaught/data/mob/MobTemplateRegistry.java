package com.codetaylor.mc.onslaught.modules.onslaught.data.mob;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Responsible for holding references to {@link MobTemplate}s and providing
 * access to them.
 */
public class MobTemplateRegistry {

  private final Map<String, MobTemplate> templateMap;
  private final List<String> idList;

  public MobTemplateRegistry(Map<String, MobTemplate> templateMap) {

    this.templateMap = templateMap;
    List<String> idList = new ArrayList<>(this.templateMap.keySet());
    Collections.sort(idList);
    this.idList = Collections.unmodifiableList(idList);
  }

  @Nullable
  public MobTemplate get(String id) {

    return this.templateMap.get(id);
  }

  /**
   * @return a list of all template ids for command tab completion
   */
  public List<String> getIdList() {

    return this.idList;
  }
}
