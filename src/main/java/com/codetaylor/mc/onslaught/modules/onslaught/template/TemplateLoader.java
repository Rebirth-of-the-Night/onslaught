package com.codetaylor.mc.onslaught.modules.onslaught.template;

import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.FilePathCreator;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.JsonFileLocator;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.template.invasion.InvasionTemplateRegistry;
import com.codetaylor.mc.onslaught.modules.onslaught.template.mob.MobTemplate;
import com.codetaylor.mc.onslaught.modules.onslaught.template.mob.MobTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.template.mob.MobTemplateRegistry;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Responsible for loading mob templates and invasion templates into the data store. */
public class TemplateLoader {

  private static final Logger LOGGER = LogManager.getLogger(TemplateLoader.class);

  private final Consumer<MobTemplateRegistry> mobTemplateRegistryConsumer;
  private final Consumer<InvasionTemplateRegistry> invasionTemplateRegistryConsumer;
  private final Path path;
  private final FilePathCreator filePathCreator;
  private final JsonFileLocator jsonFileLocator;
  private final MobTemplateLoader mobTemplateLoader;
  private final InvasionTemplateLoader invasionTemplateLoader;

  public TemplateLoader(
      Consumer<MobTemplateRegistry> mobTemplateRegistryConsumer,
      Consumer<InvasionTemplateRegistry> invasionTemplateRegistryConsumer,
      Path path,
      FilePathCreator filePathCreator,
      JsonFileLocator jsonFileLocator,
      MobTemplateLoader mobTemplateLoader,
      InvasionTemplateLoader invasionTemplateLoader) {

    this.mobTemplateRegistryConsumer = mobTemplateRegistryConsumer;
    this.invasionTemplateRegistryConsumer = invasionTemplateRegistryConsumer;
    this.path = path;
    this.filePathCreator = filePathCreator;
    this.jsonFileLocator = jsonFileLocator;
    this.mobTemplateLoader = mobTemplateLoader;
    this.invasionTemplateLoader = invasionTemplateLoader;
  }

  public boolean load() {

    return this.loadMobTemplateData() && this.loadInvasionTemplateData();
  }

  private boolean loadMobTemplateData() {

    Path mobTemplatePath = this.path.resolve(ModuleOnslaught.MOD_ID + "/templates/mob");

    try {
      long start = System.currentTimeMillis();
      this.filePathCreator.initialize(mobTemplatePath);
      List<Path> jsonFilePaths = this.jsonFileLocator.locate(mobTemplatePath);
      Map<String, MobTemplate> mobTemplateMap = this.mobTemplateLoader.load(jsonFilePaths);
      MobTemplateRegistry mobTemplateRegistry = new MobTemplateRegistry(mobTemplateMap);
      this.mobTemplateRegistryConsumer.accept(mobTemplateRegistry);
      long elapsed = System.currentTimeMillis() - start;
      ModOnslaught.LOG.info(
          String.format("Loaded %d mob templates in %d ms", mobTemplateMap.size(), elapsed));
      return true;

    } catch (Exception e) {
      String message = "Error loading mob template data";
      ModOnslaught.LOG.log(Level.SEVERE, message);
      ModOnslaught.LOG.log(Level.SEVERE, e.getMessage(), e);
      LOGGER.error(message);
      LOGGER.error(e.getMessage(), e);
    }

    return false;
  }

  private boolean loadInvasionTemplateData() {

    Path invasionTemplatePath = this.path.resolve(ModuleOnslaught.MOD_ID + "/templates/invasion");

    try {
      long start = System.currentTimeMillis();
      this.filePathCreator.initialize(invasionTemplatePath);
      List<Path> jsonFilePaths = this.jsonFileLocator.locate(invasionTemplatePath);
      Map<String, InvasionTemplate> invasionTemplateMap =
          this.invasionTemplateLoader.load(jsonFilePaths);
      InvasionTemplateRegistry invasionTemplateRegistry =
          new InvasionTemplateRegistry(invasionTemplateMap);
      this.invasionTemplateRegistryConsumer.accept(invasionTemplateRegistry);
      long elapsed = System.currentTimeMillis() - start;
      ModOnslaught.LOG.info(
          String.format(
              "Loaded %d invasion templates in %d ms", invasionTemplateMap.size(), elapsed));
      return true;

    } catch (Exception e) {
      String message = "Error loading invasion template data";
      ModOnslaught.LOG.log(Level.SEVERE, message);
      ModOnslaught.LOG.log(Level.SEVERE, e.getMessage(), e);
      LOGGER.error(message);
      LOGGER.error(e.getMessage(), e);
    }

    return false;
  }
}
