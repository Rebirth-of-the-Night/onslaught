package com.codetaylor.mc.onslaught.modules.onslaught.data;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateRegistry;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.JsonFileLocator;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.PathCreator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Responsible for loading all mod data into the data store.
 */
public class DataLoader {

  private final DataStore dataStore;
  private final Path path;
  private final PathCreator pathCreator;
  private final JsonFileLocator jsonFileLocator;
  private final MobTemplateLoader mobTemplateLoader;

  public DataLoader(
      DataStore dataStore,
      Path path,
      PathCreator pathCreator,
      JsonFileLocator jsonFileLocator,
      MobTemplateLoader mobTemplateLoader
  ) {

    this.dataStore = dataStore;
    this.path = path;
    this.pathCreator = pathCreator;
    this.jsonFileLocator = jsonFileLocator;
    this.mobTemplateLoader = mobTemplateLoader;
  }

  public boolean load() {

    return this.loadMobTemplateData();
  }

  private boolean loadMobTemplateData() {

    Path mobTemplatePath = this.path.resolve(ModuleOnslaught.MOD_ID + "/templates/mob");

    try {
      long start = System.currentTimeMillis();
      this.pathCreator.initialize(mobTemplatePath);
      List<Path> jsonFilePaths = this.jsonFileLocator.locate(mobTemplatePath);
      Map<String, MobTemplate> mobTemplateMap = this.mobTemplateLoader.load(jsonFilePaths);
      MobTemplateRegistry mobTemplateRegistry = new MobTemplateRegistry(mobTemplateMap);
      this.dataStore.setMobTemplateRegistry(mobTemplateRegistry);
      long elapsed = System.currentTimeMillis() - start;
      ModOnslaught.LOG.info(String.format("Loaded %d mob templates in %d ms", mobTemplateMap.size(), elapsed));
      return true;

    } catch (Exception e) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error loading mob template data");
      ModOnslaught.LOG.log(Level.SEVERE, e.getMessage(), e);
    }

    return false;
  }
}
