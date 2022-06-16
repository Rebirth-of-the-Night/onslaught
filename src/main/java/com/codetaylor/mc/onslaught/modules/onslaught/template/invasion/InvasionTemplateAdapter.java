package com.codetaylor.mc.onslaught.modules.onslaught.template.invasion;

import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;
import com.codetaylor.mc.onslaught.modules.onslaught.lib.StagesTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

/** Responsible for consuming a json string and returning a map of {@link InvasionTemplate}s. */
public class InvasionTemplateAdapter {

  private final Gson gson;

  public InvasionTemplateAdapter() {

    this.gson =
        new GsonBuilder().registerTypeAdapter(Stages.class, StagesTypeAdapter.INSTANCE).create();
  }

  public Map<String, InvasionTemplate> adapt(String json) {

    Type type =
        new TypeToken<Map<String, InvasionTemplate>>() {
          //
        }.getType();

    return this.gson.fromJson(json, type);
  }
}
