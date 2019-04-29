package de.cherry.webinterpreter.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHelper {

  // return supported MIME Types
  public static boolean isFile(String fileRequested) {
    return fileRequested.equals("/") || fileRequested.contains(".");
  }

  public static String readFileData(File file) throws IOException {
    return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
  }
}
