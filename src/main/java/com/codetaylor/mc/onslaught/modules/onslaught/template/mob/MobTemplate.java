package com.codetaylor.mc.onslaught.modules.onslaught.data.mob;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Responsible for holding mob template data read from json files.
 */
public class MobTemplate {

  public String id;
  public MobTemplateEffect[] effects = {};
  public String[] extraLootTables = {};
  public NBTTagCompound nbt = new NBTTagCompound();

}
