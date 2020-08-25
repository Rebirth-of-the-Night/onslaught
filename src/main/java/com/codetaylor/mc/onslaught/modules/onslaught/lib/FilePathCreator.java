package com.codetaylor.mc.onslaught.modules.onslaught.lib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Responsible for creating the given path if it doesn't exist.
 */
public class FilePathCreator {

  public void initialize(Path path) throws IOException {

    if (!Files.exists(path)) {

      try {
        Files.createDirectories(path);

      } catch (Exception e) {
        throw new IOException("Unable to create path: " + path, e);
      }
    }
  }
}
