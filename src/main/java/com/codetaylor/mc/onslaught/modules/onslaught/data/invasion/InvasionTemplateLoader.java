package com.codetaylor.mc.onslaught.modules.onslaught.data.invasion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for loading {@link InvasionTemplate}s from a list of json file paths.
 */
public class InvasionTemplateLoader {

  private final InvasionTemplateAdapter adapter;

  public InvasionTemplateLoader(InvasionTemplateAdapter adapter) {

    this.adapter = adapter;
  }

  public Map<String, InvasionTemplate> load(List<Path> pathList) throws IOException {

    Map<String, InvasionTemplate> result = new HashMap<>();

    for (Path path : pathList) {
      String content = new String(Files.readAllBytes(path));
      Map<String, InvasionTemplate> adapt = this.adapter.adapt(content);

      for (Map.Entry<String, InvasionTemplate> entry : adapt.entrySet()) {
        String key = entry.getKey();

        if (result.containsKey(key)) {
          throw new IOException("Duplicate invasion template key: " + key);
        }

        result.put(key, entry.getValue());
      }
    }

    return result;
  }
}