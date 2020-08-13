package com.codetaylor.mc.onslaught.modules.onslaught.data;

import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateRegistry;

import java.util.Collections;

/**
 * Responsible for holding references to all loaded mod data.
 */
public class DataStore {

  private MobTemplateRegistry mobTemplateRegistry;

  public DataStore() {

    this.setMobTemplateRegistry(new MobTemplateRegistry(Collections.emptyMap()));
  }

  public MobTemplateRegistry getMobTemplateRegistry() {

    return mobTemplateRegistry;
  }

  void setMobTemplateRegistry(MobTemplateRegistry mobTemplateRegistry) {

    this.mobTemplateRegistry = mobTemplateRegistry;
  }
}
