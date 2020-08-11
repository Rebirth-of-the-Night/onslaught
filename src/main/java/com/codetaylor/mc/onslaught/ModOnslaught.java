package com.codetaylor.mc.onslaught;

import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.module.ModuleManager;
import com.codetaylor.mc.onslaught.modules.onslaught.ModuleOnslaught;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

import java.util.HashSet;
import java.util.Set;

@Mod(
    modid = ModOnslaught.MOD_ID,
    version = ModOnslaught.VERSION,
    name = ModOnslaught.NAME,
    dependencies = ModOnslaught.DEPENDENCIES
)
public class ModOnslaught {

  public static final String MOD_ID = "onslaught";
  public static final String VERSION = "@@VERSION@@";
  public static final String NAME = "Onslaught";
  public static final String DEPENDENCIES = "";

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

  private Set<Class<? extends ModuleBase>> registeredModules = new HashSet<>();

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
