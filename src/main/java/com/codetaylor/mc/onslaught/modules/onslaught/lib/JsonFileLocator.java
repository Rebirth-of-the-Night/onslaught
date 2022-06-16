package com.codetaylor.mc.onslaught.modules.onslaught.lib;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Responsible for iterating files in a folder and returning a list of paths to each json file. */
public class JsonFileLocator {

  public List<Path> locate(Path path) throws IOException {

    DirectoryStream<Path> stream;

    try {
      stream =
          Files.newDirectoryStream(
              path,
              entry -> Files.isRegularFile(entry) && entry.toFile().getName().endsWith(".json"));

    } catch (Exception e) {
      throw new IOException("Error iterating files in path: " + path, e);
    }

    List<Path> result = new ArrayList<>();

    for (Path pathFile : stream) {
      result.add(pathFile);
    }

    return result;
  }
}
