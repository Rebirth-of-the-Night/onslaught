package com.codetaylor.mc.onslaught.modules.onslaught.lib;

import com.codetaylor.mc.athenaeum.integration.gamestages.Stages;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is registered with the Gson instance used to deserialize Onslaught json files. It is
 * responsible for converting the json into a {@link Stages} object. This adapter does not support
 * serialization and will throw an {@link UnsupportedOperationException} on write.
 */
public class StagesTypeAdapter extends TypeAdapter<Stages> {

  public static final StagesTypeAdapter INSTANCE = new StagesTypeAdapter();

  @Override
  public void write(JsonWriter out, Stages value) throws IOException {

    throw new UnsupportedOperationException("This adapter does not support serialization");
  }

  @Override
  public Stages read(JsonReader in) throws IOException {

    return this.readObject(in);
  }

  private Stages readObject(JsonReader in) throws IOException {

    in.beginObject();

    String type = in.nextName().toLowerCase();
    Stages result;

    switch (type) {
      case "and":
        result = this.and(in);
        break;

      case "or":
        result = this.or(in);
        break;

      case "not":
        result = this.not(in);
        break;

      default:
        throw new JsonSyntaxException("Unrecognized type: " + type);
    }

    in.endObject();

    return result;
  }

  private void readList(JsonReader in, List<Object> result) throws IOException {

    in.beginArray();

    boolean keepReading = true;

    while (keepReading) {
      keepReading = this.readListElement(in, result);
    }

    in.endArray();
  }

  private boolean readListElement(JsonReader in, List<Object> result) throws IOException {

    JsonToken peek = in.peek();

    switch (peek) {
      case STRING:
        result.add(in.nextString());
        return true;

      case BEGIN_OBJECT:
        result.add(this.readObject(in));
        return true;

      case END_ARRAY:
        return false;

      default:
        throw new JsonSyntaxException("Unexpected token: " + peek);
    }
  }

  private Stages and(JsonReader in) throws IOException {

    List<Object> stages = new ArrayList<>();
    this.readList(in, stages);
    return Stages.and(stages.toArray(new Object[0]));
  }

  private Stages or(JsonReader in) throws IOException {

    List<Object> stages = new ArrayList<>();
    this.readList(in, stages);
    return Stages.or(stages.toArray(new Object[0]));
  }

  private Stages not(JsonReader in) throws IOException {

    List<Object> stages = new ArrayList<>();
    this.readListElement(in, stages);

    if (stages.get(0) instanceof String) {
      return Stages.not((String) stages.get(0));

    } else {
      return Stages.not((Stages) stages.get(0));
    }
  }
}
