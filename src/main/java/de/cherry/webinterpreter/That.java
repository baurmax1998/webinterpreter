package de.cherry.webinterpreter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cherry.webinterpreter.data.Entity;
import de.cherry.webinterpreter.data.Manager;
import de.cherry.webinterpreter.data.PersonManager;
import de.cherry.webinterpreter.data.Property;
import de.cherry.webinterpreter.server.Server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class That implements BiFunction<String, String, String> {
  private static That ourInstance = new That();

  private Map<String, Manager> managers = new HashMap<>();

  public static That getInstance() {
    return ourInstance;
  }

  private That() {
    addManager(new PersonManager());
  }

  private That addManager(Manager manager) {
    this.managers.put(manager.getId(), manager);
    return this;
  }

  public void run() {
    Server.start(this);
  }

  @Override
  public String apply(String path, String param) {
    Object someThing = null;

    if (path.equals("")) {

    } else if (path.startsWith("/list")) {
      path = path.substring("/list".length());
      if (path.startsWith("/ref")) {
        path = path.substring("/ref".length());
        Manager manager = managers.get(path.split("/")[0]);
        someThing = manager.getRefs();
      } else {
        Manager manager = managers.get(path.split("/")[0]);
        someThing = manager.getAll();
      }

    } else if (path.startsWith("/ref")) {
      path = path.substring("/ref".length());
      Manager manager = managers.get(path.split("/")[0]);
      someThing = manager.getRefs();
    } else {
      String[] split = path.split("/");
      Manager manager = managers.get(split[1]);
      if (split.length == 2)
        someThing = manager.getAll();
      else if (split.length == 3)
        someThing = manager.get(split[1]);
      else
        throw new RuntimeException("no valide Paht: " + path);
    }

    try {
      return new ObjectMapper().writeValueAsString(someThing);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
