package de.cherry.webinterpreter.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.function.BiFunction;

import static de.cherry.webinterpreter.server.FileHelper.*;
import static de.cherry.webinterpreter.server.HeaderHelper.*;

public class Server implements Runnable {

  public static int PORT = 8080;
  public static File WEB_ROOT = new File("./src/main/webapp/");
  public static String DEFAULT_FILE = "index.html";

  private BufferedReader in = null;
  private PrintWriter out = null;
  private BufferedOutputStream dataOut = null;
  private String path = null;

  // Client Connection via Socket Class
  private Socket connect;
  private BiFunction<String, String, String> that;

  public Server(Socket c, BiFunction<String, String, String> that) {
    connect = c;
    this.that = that;
  }

  public static void start(BiFunction<String, String, String> that) {
    try {
      ServerSocket serverConnect = new ServerSocket(Server.PORT);
      System.out.println("Server started.\nListening for connections on port : " + Server.PORT + " ...\n");
      // we listen until user stops server execution
      while (true) {
        Server myServer = new Server(serverConnect.accept(), that);
        System.out.println("Connecton opened. (" + new Date() + ")");
        // create dedicated thread to manage the client connection
        Thread thread = new Thread(myServer);
        thread.start();
      }

    } catch (IOException e) {
      System.err.println("Server Connection error : " + e.getMessage());
    }
  }


  public void run() {
    // we manage our particular client connection
    try {
      // we read characters from the client via input stream on the socket
      in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
      // we get character output stream to client (for headers)
      out = new PrintWriter(connect.getOutputStream());
      // get binary output stream to client (for requested data)
      dataOut = new BufferedOutputStream(connect.getOutputStream());

      StringTokenizer parse = new StringTokenizer(in.readLine());
      String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
      path = parse.nextToken().toLowerCase();
      if (method.equals("GET")) {
        if (isFile(path)) {
          if (path.equals("/"))
            path += DEFAULT_FILE;
          File file = new File(WEB_ROOT, path);
          send(file);
        } else {
          send(new Error("Http-GET is just for Files!!"));
        }
      } else if (method.equals("POST")) {
        String postData = getPostData(in);
        send(that.apply(path, postData));
      } else {
        // we return the not supported file to the client
        send(new Error("Methode: " + method + " not supported!!1!"));
      }

    } catch (FileNotFoundException e) {
      try {
        send(new Error("File: " + path + " not Found!"));
      } catch (IOException ioe) {
        System.err.println("Error with file not found exception : " + ioe.getMessage());
      }
    } catch (IOException ioe) {
      System.err.println("Server error : " + ioe);
    } finally {
      try {
        in.close();
        out.close();
        dataOut.close();
        connect.close(); // we close socket connection
      } catch (Exception e) {
        System.err.println("Error closing stream : " + e.getMessage());
      }
      System.out.println("Connection closed.\n");
    }
  }

  private void send(Object o) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    send("application/json", objectMapper.writeValueAsString(o));
  }

  private void send(File file) throws IOException {
    String fileData = readFileData(file);
    String[] split = file.getAbsolutePath().split("\\.");
    String fileExtension = split[split.length - 1];
    String type = "text/html";
    if (fileExtension.equals("js"))
      type = "application/javascript";
    else if (fileExtension.equals("css"))
      type = "text/css";
    send(type, fileData);
  }

  private void send(String contentType, String content) throws IOException {
    // we send HTTP Headers with data to client
    out.println("HTTP/1.1 200 OK");
    out.println("Server: Java HTTP Server from Max : 1.0");
    out.println("Date: " + new Date());
    out.println("Content-type: " + contentType);
    out.println("Content-length: " + content.length());
    out.println(); // blank line between headers and content, very important !
    out.flush(); // flush character output stream buffer
    //send Data
    dataOut.write(content.getBytes(), 0, content.length());
    dataOut.flush();
  }

}
