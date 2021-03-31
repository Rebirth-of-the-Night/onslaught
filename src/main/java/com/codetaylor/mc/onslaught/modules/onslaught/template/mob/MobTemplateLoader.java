package com.codetaylor.mc.onslaught.modules.onslaught.template.mob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Responsible for loading {@link MobTemplate}s from a list of json file paths. */
public class MobTemplateLoader {

  private final MobTemplateAdapter adapter;

  public MobTemplateLoader(MobTemplateAdapter adapter) {

    this.adapter = adapter;
  }

  public Map<String, MobTemplate> load(List<Path> pathList) throws IOException {

    Map<String, MobTemplate> result = new HashMap<>();

    for (Path path : pathList) {
      String content = new String(Files.readAllBytes(path));
      Map<String, MobTemplate> adapt = this.adapter.adapt(content);

      for (Map.Entry<String, MobTemplate> entry : adapt.entrySet()) {
        String key = entry.getKey();

        if (result.containsKey(key)) {
          throw new IOException("Duplicate mob template key: " + key);
        }

        result.put(key, entry.getValue());
      }
    }

    return result;
  }
}
