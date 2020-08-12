package com.codetaylor.mc.onslaught.modules.onslaught.lib;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.output.StringBuilderWriter;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * This class is registered with the Gson instance used to deserialize
 * Onslaught json files. It is responsible for converting the json into
 * an {@link NBTTagCompound}. This adapter does not support serialization
 * and will throw an {@link UnsupportedOperationException} on write.
 */
public class NBTTagCompoundTypeAdapter
    extends TypeAdapter<NBTTagCompound> {

  public static final NBTTagCompoundTypeAdapter INSTANCE = new NBTTagCompoundTypeAdapter();

  private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
  private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");

  @Override
  public void write(JsonWriter out, NBTTagCompound value) {

    throw new UnsupportedOperationException("This adapter does not support serialization");
  }

  @Override
  public NBTTagCompound read(JsonReader in) throws IOException {

    try {
      StringBuilder builder = new StringBuilder();
      StringBuilderWriter stringBuilderWriter = new StringBuilderWriter(builder);
      JsonWriter jsonWriter = new JsonWriter(stringBuilderWriter);

      this.writeObject(in, jsonWriter);

      String jsonString = builder.toString();
      return JsonToNBT.getTagFromJson(jsonString);

    } catch (NBTException e) {
      throw new IOException("Error reading NBT", e);
    }
  }

  private boolean write(JsonReader in, JsonWriter out) throws IOException {

    JsonToken peek = in.peek();

    switch (peek) {

      case BEGIN_ARRAY:
        this.writeArray(in, out);
        return true;

      case BEGIN_OBJECT:
        this.writeObject(in, out);
        return true;

      case BOOLEAN:
        out.value(in.nextBoolean());
        return true;

      case NAME:
        out.name(in.nextName());
        return true;

      case NULL:
        out.nullValue();
        in.nextNull();
        return true;

      case NUMBER:
        this.writeNumber(in.nextString(), out);
        return true;

      case STRING:
        out.value(in.nextString());
        return true;

      case END_ARRAY:
      case END_DOCUMENT:
      case END_OBJECT:
      default:
        return false;
    }
  }

  private void writeArray(JsonReader in, JsonWriter out) throws IOException {

    in.beginArray();
    out.beginArray();

    boolean keepReading = true;

    while (keepReading) {
      keepReading = this.write(in, out);
    }

    in.endArray();
    out.endArray();
  }

  private void writeObject(JsonReader in, JsonWriter out) throws IOException {

    in.beginObject();
    out.beginObject();

    boolean keepReading = true;

    while (keepReading) {
      keepReading = this.write(in, out);
    }

    in.endObject();
    out.endObject();
  }

  private void writeNumber(String s, JsonWriter out) throws IOException {

    if (INT_PATTERN.matcher(s).matches()) {
      out.value(Integer.parseInt(s));

    } else if (DOUBLE_PATTERN.matcher(s).matches()) {
      out.value(Double.parseDouble(s));

    } else {
      out.value(s);
    }
  }
}
