package com.codetaylor.mc.onslaught.modules.onslaught;

import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIChaseLongDistanceInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIMiningInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.ai.EntityAIPlayerTargetInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandReload;
import com.codetaylor.mc.onslaught.modules.onslaught.command.CommandSummon;
import com.codetaylor.mc.onslaught.modules.onslaught.data.DataLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.data.DataStore;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateAdapter;
import com.codetaylor.mc.onslaught.modules.onslaught.data.mob.MobTemplateLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.event.CustomLootTableManagerInjectionEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.EntityAiInjectionEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.event.ExtraLootInjectionEventHandler;
import com.codetaylor.mc.onslaught.modules.onslaught.factory.MobTemplateEntityFactory;
import com.codetaylor.mc.onslaught.modules.onslaught.factory.MobTemplateEntityFactoryEffectApplicator;
import com.codetaylor.mc.onslaught.modules.onslaught.factory.MobTemplateEntityFactoryLootTableApplicator;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.JsonFileLocator;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.PathCreator;
import com.codetaylor.mc.onslaught.modules.onslaught.loot.CustomLootTableManagerInjector;
import com.codetaylor.mc.onslaught.modules.onslaught.loot.ExtraLootInjector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

public class ModuleOnslaught
    extends ModuleBase {

  public static final String MOD_ID = ModOnslaught.MOD_ID;

  private final DataStore dataStore;
  private DataLoader dataLoader;

  public ModuleOnslaught() {

    super(0, MOD_ID);
    this.dataStore = new DataStore();
  }

  @Override
  public void onPreInitializationEvent(FMLPreInitializationEvent event) {

    super.onPreInitializationEvent(event);

    File modConfigurationDirectory = event.getModConfigurationDirectory();
    Path modConfigurationPath = modConfigurationDirectory.toPath();
    PathCreator pathCreator = new PathCreator();

    // -------------------------------------------------------------------------
    // - Json Templates
    // -------------------------------------------------------------------------

    this.dataLoader = new DataLoader(
        this.dataStore,
        modConfigurationPath,
        pathCreator,
        new JsonFileLocator(),
        new MobTemplateLoader(
            new MobTemplateAdapter()
        )
    );
    this.dataLoader.load();

    // -------------------------------------------------------------------------
    // - Extra Loot Injection
    // -------------------------------------------------------------------------

    try {
      pathCreator.initialize(modConfigurationPath.resolve(MOD_ID + "/loot"));

    } catch (IOException e) {
      ModOnslaught.LOG.log(Level.SEVERE, "Error creating path: " + MOD_ID + "/loot");
      ModOnslaught.LOG.log(Level.SEVERE, e.getMessage(), e);
      throw new RuntimeException(e);
    }

    MinecraftForge.EVENT_BUS.register(new CustomLootTableManagerInjectionEventHandler(
        new CustomLootTableManagerInjector(
            modConfigurationPath.resolve(MOD_ID + "/loot").toFile()
        )
    ));

    MinecraftForge.EVENT_BUS.register(new ExtraLootInjectionEventHandler(
        new ExtraLootInjector()
    ));

    // -------------------------------------------------------------------------
    // - AI Injection
    // -------------------------------------------------------------------------

    MinecraftForge.EVENT_BUS.register(new EntityAiInjectionEventHandler(
        new EntityAIPlayerTargetInjector(),
        new EntityAIChaseLongDistanceInjector(),
        new EntityAIMiningInjector()
    ));
  }

  @Override
  public void onServerStartingEvent(FMLServerStartingEvent event) {

    super.onServerStartingEvent(event);

    // -------------------------------------------------------------------------
    // - Command Registration
    // -------------------------------------------------------------------------

    event.registerServerCommand(new CommandSummon(
        this.dataStore,
        new MobTemplateEntityFactory(
            new MobTemplateEntityFactoryEffectApplicator(),
            new MobTemplateEntityFactoryLootTableApplicator()
        )
    ));

    event.registerServerCommand(new CommandReload(
        this.dataLoader
    ));
  }
}
