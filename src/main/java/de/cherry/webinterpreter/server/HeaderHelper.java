package de.cherry.webinterpreter.server;

import java.io.BufferedReader;
import java.io.IOException;

public class HeaderHelper {

  public static String getPostData(BufferedReader in) throws IOException {
    int postData1 = -1;
    String line;
    while ((line = in.readLine()) != null && (line.length() != 0)) {
      System.out.println("HTTP-Header: " + line);
      if (line.contains("Content-Length:"))
        postData1 = new Integer(
            line.substring(
                line.indexOf("Content-Length:") + 16));
    }
    String postData = "";
    if (postData1 > 0) {
      char[] charArray = new char[postData1];
      in.read(charArray, 0, postData1);
      postData = new String(charArray);
    }
    return postData;
  }
}
