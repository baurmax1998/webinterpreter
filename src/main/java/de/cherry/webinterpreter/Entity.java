package de.cherry.webinterpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Entity {
  public String name;
  public String description;
  public String idProperty;
  public List<Property> properties = new ArrayList<>();

  public Entity(String name, String description, String idProperty) {
    this.name = name;
    this.description = description;
    this.idProperty = idProperty;
  }

  public Entity setProps(Property... properties) {
    this.properties = Arrays.asList(properties);
    return this;
  }
}
