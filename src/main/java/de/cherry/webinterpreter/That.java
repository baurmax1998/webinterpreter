package de.cherry.webinterpreter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cherry.webinterpreter.server.Server;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class That implements BiFunction<String, String, String> {
  private static That ourInstance = new That();

  public static That getInstance() {
    return ourInstance;
  }

  private That() {
  }

  public void run() {
    Server.start(this);
  }

  @Override
  public String apply(String path, String param) {
    Object someThing = null;
    if (path.equals("/person")) {
      someThing = new Entity(
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
              , "ref/friend"
              , "Bruce"
              , "my best friend"
          )
          , new Property(
              "friends"
              , "list/ref/friend"
              , ""
              , "all my friends"
          )
          , new Property(
              "completeFriends"
              , "list/friend"
              , ""
              , "all my friends with details"
          )
      );
    }

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

    if (path.endsWith("/ref/friend")) {
      someThing = friends.stream().map(x -> x.properties.get(0).content).collect(Collectors.toList());
    }

    if (path.endsWith("/list/friend")) {
      someThing = friends;
    }

      try {
      return new ObjectMapper().writeValueAsString(someThing);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
