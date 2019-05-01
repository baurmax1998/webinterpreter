package de.cherry.webinterpreter.data;

public class Property {
  public String name;
  public String type;
  public String content;
  public String description;
  public boolean editable = true;

  public Property(String name, String type, String content, String description) {
    this.name = name;
    this.type = type;
    this.content = content;
    this.description = description;
  }
}
