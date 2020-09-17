package com.codetaylor.mc.onslaught.modules.onslaught.template.invasion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for loading {@link InvasionTemplate}s from a list of json file paths.
 * <p>
 * Throws exceptions when a duplicate invasion key is used, an invasion contains
 * no waves, and when an invasion wave contains no mobs.
 */
public class InvasionTemplateLoader {

  private final InvasionTemplateAdapter adapter;

  public InvasionTemplateLoader(InvasionTemplateAdapter adapter) {

    this.adapter = adapter;
  }

  public Map<String, InvasionTemplate> load(List<Path> pathList) throws Exception {

    Map<String, InvasionTemplate> result = new HashMap<>();

    for (Path path : pathList) {
      String content = new String(Files.readAllBytes(path));
      Map<String, InvasionTemplate> adapt = this.adapter.adapt(content);

      for (Map.Entry<String, InvasionTemplate> entry : adapt.entrySet()) {
        String key = entry.getKey();

        if (result.containsKey(key)) {
          throw new Exception("Duplicate invasion template key: " + key);
        }

        InvasionTemplate template = entry.getValue();

        if (template.waves.length == 0) {
          throw new Exception("Invasion must contain at least one wave: " + key);
        }

        for (InvasionTemplateWave wave : template.waves) {

          if (wave.groups.length == 0) {
            throw new Exception("Invasion wave must contain at least one mob: " + key);
          }

          for (InvasionTemplateWave.Group group : wave.groups) {

            if (group.mobs.length == 0) {
              throw new Exception("Invasion wave group must contain at least one mob: " + key);
            }
          }
        }

        result.put(key, template);
      }
    }

    return result;
  }
}