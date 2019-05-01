package de.cherry.webinterpreter.data;

import java.util.List;

public interface Manager {

  String getId();

  Entity get(String id);

  List<Entity> getAll();

  List<String> getRefs();
}
