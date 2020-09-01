package com.codetaylor.mc.onslaught.modules.onslaught.data;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.data.invasion.InvasionTemplateRegistry;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateRegistry;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.FilePathCreator;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.JsonFileLocator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Responsible for loading mob templates and invasion templates into the data store.
 */
public class DataLoader {

  private final DataStore dataStore;
  private final Path path;
  private final FilePathCreator filePathCreator;
  private final JsonFileLocator jsonFileLocator;
  private final MobTemplateLoader mobTemplateLoader;
  private final InvasionTemplateLoader invasionTemplateLoader;

  public DataLoader(
      DataStore dataStore,
      Path path,
      FilePathCreator filePathCreator,
      JsonFileLocator jsonFileLocator,
      MobTemplateLoader mobTemplateLoader,
      InvasionTemplateLoader invasionTemplateLoader
  ) {

    this.dataStore = dataStore;
    this.path = path;
    this.filePathCreator = filePathCreator;
    this.jsonFileLocator = jsonFileLocator;
    this.mobTemplateLoader = mobTemplateLoader;
    this.invasionTemplateLoader = invasionTemplateLoader;
  }

  public boolean load() {

    return this.loadMobTemplateData()
        && this.loadInvasionTemplateData();
  }

  private boolean loadMobTemplateData() {

    Path mobTemplatePath = this.path.resolve(ModuleOnslaught.MOD_ID + "/templates/mob");

    try {
      long start = System.currentTimeMillis();
      this.filePathCreator.initialize(mobTemplatePath);
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

  private boolean loadInvasionTemplateData() {

    Path invasionTemplatePath = this.path.resolve(ModuleOnslaught.MOD_ID + "/templates/invasion");

    try {
      long start = System.currentTimeMillis();
      this.filePathCreator.initialize(invasionTemplatePath);
      List<Path> jsonFilePaths = this.jsonFileLocator.locate(invasionTemplatePath);
      Map<String, InvasionTemplate> invasionTemplateMap = this.invasionTemplateLoader.load(jsonFilePaths);
      InvasionTemplateRegistry invasionTemplateRegistry = new InvasionTemplateRegistry(invasionTemplateMap);
      this.dataStore.setInvasionTemplateRegistry(invasionTemplateRegistry);
      long elapsed = System.currentTimeMillis() - start;
      ModOnslaught.LOG.info(String.format("Loaded %d invasion templates in %d ms", invasionTemplateMap.size(), elapsed));
      return true;

    } catch (Exception e) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error loading invasion template data");
      ModOnslaught.LOG.log(Level.SEVERE, e.getMessage(), e);
    }

    return false;
  }
}
