package com.codetaylor.mc.onslaught;

import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.module.ModuleManager;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.LogFormatter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Mod(
    modid = ModOnslaught.MOD_ID,
    useMetadata=true
)
public class ModOnslaught {

  public static final String MOD_ID = "onslaught";

  public static Logger LOG;

  @SuppressWarnings("unused")
  @Mod.Instance
  public static ModOnslaught INSTANCE;

  public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {

    @Override
    public ItemStack getTabIconItem() {

      return new ItemStack(Blocks.STONE);
    }
  };

  private final ModuleManager moduleManager;

  private final Set<Class<? extends ModuleBase>> registeredModules = new HashSet<>();

  public ModOnslaught() {

    this.moduleManager = new ModuleManager(MOD_ID);
  }

  @Mod.EventHandler
  public void onConstructionEvent(FMLConstructionEvent event) {

    // --- MODULES ---

    this.moduleManager.registerModules(
        ModuleOnslaught.class
    );

    // --- POST ---

    this.moduleManager.onConstructionEvent();
    this.moduleManager.routeFMLStateEvent(event);
  }

  private void registerModule(Class<? extends ModuleBase> moduleClass) {

    this.moduleManager.registerModules(moduleClass);
    this.registeredModules.add(moduleClass);
  }

  public boolean isModuleEnabled(Class<? extends ModuleBase> moduleClass) {

    return this.registeredModules.contains(moduleClass);
  }

  @Mod.EventHandler
  public void onPreInitializationEvent(FMLPreInitializationEvent event) {

    try {
      LOG = Logger.getLogger(ModOnslaught.class.getName());
      FileHandler handler = new FileHandler("logs/" + MOD_ID + ".log", 1024 * 256, 5, true);
      handler.setFormatter(new LogFormatter());
      LOG.addHandler(handler);
      LOG.setUseParentHandlers(false);
      LOG.setLevel(Level.FINE);
      LOG.info("Initialized logger");

    } catch (IOException e) {
      LogManager.getLogger(ModOnslaught.class.getName()).error("Error initializing Onslaught log", e);
    }

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onInitializationEvent(FMLInitializationEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onPostInitializationEvent(FMLPostInitializationEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onLoadCompleteEvent(FMLLoadCompleteEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onServerAboutToStartEvent(FMLServerAboutToStartEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onServerStartingEvent(FMLServerStartingEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onServerStartedEvent(FMLServerStartedEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onServerStoppingEvent(FMLServerStoppingEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

  @Mod.EventHandler
  public void onServerStoppedEvent(FMLServerStoppedEvent event) {

    this.moduleManager.routeFMLStateEvent(event);
  }

}
