package de.cherry.webinterpreter.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PersonManager implements Manager {

  List<Entity> friends = Arrays.asList(
      new Entity(
          "person"
          , "a person like u and me"
          , "name"
      ).setProps(
          new Property(
              "name"
              , "String"
              , "steve"
              , "everyone has one")
          , new Property(
              "age"
              , "Number"
              , "16"
              , "how old are u?")
      )
      , new Entity(
          "person"
          , "a person like u and me"
          , "name"
      ).setProps(
          new Property(
              "name"
              , "String"
              , "roger"
              , "everyone has one")
          , new Property(
              "age"
              , "Number"
              , "12"
              , "how old are u?")
      )
  );

  @Override
  public String getId() {
    return "person";
  }

  @Override
  public Entity get(String id) {
    return new Entity(
        "person"
        , "a person like u and me"
        , "name"
    ).setProps(
        new Property(
            "name"
            , "String"
            , "bruce"
            , "everyone has one")
        , new Property(
            "age"
            , "Number"
            , "1"
            , "how old are u?")
        , new Property(
            "alive"
            , "Bool"
            , "true"
            , "well?")
        , new Property(
            "bestFriend"
            , "ref/person"
            , "Bruce"
            , "my best friend"
        )
        , new Property(
            "friends"
            , "list/ref/person"
            , ""
            , "all my friends"
        )
        , new Property(
            "completeFriends"
            , "list/person"
            , ""
            , "all my friends with details"
        )
    );
  }

  @Override
  public List<Entity> getAll() {
    return friends;
  }

  @Override
  public List<String> getRefs() {
    return friends.stream().map(x -> x.properties.get(0).content).collect(Collectors.toList());
  }
}
