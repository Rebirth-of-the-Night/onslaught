package com.codetaylor.mc.onslaught.modules.onslaught.data.invasion;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Responsible for holding references to {@link InvasionTemplate}s and providing
 * access to them.
 */
public class InvasionTemplateRegistry {

  private final Map<String, InvasionTemplate> templateMap;
  private final List<String> idList;

  public InvasionTemplateRegistry(Map<String, InvasionTemplate> templateMap) {

    this.templateMap = templateMap;
    List<String> idList = new ArrayList<>(this.templateMap.keySet());
    Collections.sort(idList);
    this.idList = Collections.unmodifiableList(idList);
  }

  @Nullable
  public InvasionTemplate get(String id) {

    return this.templateMap.get(id);
  }

  public boolean has(String id) {

    return this.templateMap.containsKey(id);
  }

  public Set<Map.Entry<String, InvasionTemplate>> getAll() {

    return Collections.unmodifiableMap(this.templateMap).entrySet();
  }

  /**
   * @return a list of all template ids for command tab completion
   */
  public List<String> getIdList() {

    return this.idList;
  }
}
