package com.vmware.vra.jenkinsplugin.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Map;

public class JSONUtils {
  private static final Type mapStringObject = new TypeToken<Map<String, Object>>() {}.getType();

  private static final ThreadLocal<Gson> cachedGson = new ThreadLocal<>();

  public static Gson getGson() {

    Gson gson = cachedGson.get();
    if (gson == null) {
      gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
      cachedGson.set(gson);
    }
    return gson;
  }

  public static <T> T fromJson(final String json, final Class<T> clazz) {
    return getGson().fromJson(json, clazz);
  }

  public static Map<String, Object> fromJsonToMap(final String json) {
    return getGson().fromJson(json, mapStringObject);
  }

  public static String toJson(final Object o) {
    return getGson().toJson(o);
  }

  public static class DateTypeAdapter extends TypeAdapter<Date> {

    private DateFormat dateFormat;

    public DateTypeAdapter() {}

    public DateTypeAdapter(final DateFormat dateFormat) {
      this.dateFormat = dateFormat;
    }

    public void setFormat(final DateFormat dateFormat) {
      this.dateFormat = dateFormat;
    }

    @Override
    public void write(final JsonWriter out, final Date date) throws IOException {
      if (date == null) {
        out.nullValue();
      } else {
        final String value;
        if (dateFormat != null) {
          value = dateFormat.format(date);
        } else {
          value = ISO8601Utils.format(date, true);
        }
        out.value(value);
      }
    }

    @Override
    public Date read(final JsonReader in) throws IOException {
      try {
        if (in.peek() == JsonToken.NULL) {
          in.nextNull();
          return null;
        } else {
          final String date = in.nextString();
          try {
            if (dateFormat != null) {
              return dateFormat.parse(date);
            }
            return ISO8601Utils.parse(date, new ParsePosition(0));
          } catch (final ParseException e) {
            throw new JsonParseException(e);
          }
        }
      } catch (final IllegalArgumentException e) {
        throw new JsonParseException(e);
      }
    }
  }
}
