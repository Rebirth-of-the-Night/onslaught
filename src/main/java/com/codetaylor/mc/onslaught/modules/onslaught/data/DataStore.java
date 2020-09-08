package com.codetaylor.mc.onslaught.modules.onslaught.data;

import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateRegistry;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateRegistry;

import java.util.Collections;

/**
 * Responsible for holding references to mob templates and invasion templates.
 */
public class DataStore {

  private MobTemplateRegistry mobTemplateRegistry;
  private InvasionTemplateRegistry invasionTemplateRegistry;

  public DataStore() {

    this.setMobTemplateRegistry(new MobTemplateRegistry(Collections.emptyMap()));
    this.setInvasionTemplateRegistry(new InvasionTemplateRegistry(Collections.emptyMap()));
  }

  /**
   * The {@link MobTemplateRegistry} returned by this method should not be cached
   * as it will change when the templates are reloaded.
   *
   * @return the {@link MobTemplateRegistry}
   */
  public MobTemplateRegistry getMobTemplateRegistry() {

    return this.mobTemplateRegistry;
  }

  public void setMobTemplateRegistry(MobTemplateRegistry mobTemplateRegistry) {

    this.mobTemplateRegistry = mobTemplateRegistry;
  }

  /**
   * The {@link InvasionTemplateRegistry} returned by this method should not be
   * cached as it will change when the templates are reloaded.
   *
   * @return the {@link InvasionTemplateRegistry}
   */
  public InvasionTemplateRegistry getInvasionTemplateRegistry() {

    return this.invasionTemplateRegistry;
  }

  public void setInvasionTemplateRegistry(InvasionTemplateRegistry invasionTemplateRegistry) {

    this.invasionTemplateRegistry = invasionTemplateRegistry;
  }
}
