package com.codetaylor.mc.onslaught.modules.onslaught.data.mob;

import com.codetaylor.mc.onslaught.modules.onslaught.lib.NBTTagCompoundTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Responsible for consuming a json string and returning a map of
 * {@link MobTemplate}s.
 */
public class MobTemplateAdapter {

  private final Gson gson;

  public MobTemplateAdapter() {

    this.gson = new GsonBuilder()
        .registerTypeAdapter(NBTTagCompound.class, NBTTagCompoundTypeAdapter.INSTANCE)
        .create();
  }

  public Map<String, MobTemplate> adapt(String json) {

    Type type = new TypeToken<Map<String, MobTemplate>>() {
      //
    }.getType();

    return this.gson.fromJson(json, type);
  }
}
