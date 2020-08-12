package com.codetaylor.mc.onslaught.modules.onslaught;

import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.onslaught.ModOnslaught;
import com.codetaylor.mc.onslaught.modules.onslaught.data.DataLoader;
import com.codetaylor.mc.onslaught.modules.onslaught.data.DataStore;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.nio.file.Path;

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
    Path path = modConfigurationDirectory.toPath();
    this.dataLoader = new DataLoader(this.dataStore, path);
    this.reloadData();
  }

  public void reloadData() {

    this.dataLoader.load();
  }
}
