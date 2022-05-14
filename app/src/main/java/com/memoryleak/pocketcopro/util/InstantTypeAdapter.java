package com.memoryleak.pocketcopro.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    private static final String SECONDS_NAME = "seconds";
    private static final String NANOS_NAME = "nanos";

    @Override
    public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        long seconds = object.get(SECONDS_NAME).getAsLong();
        long nanos = object.get(NANOS_NAME).getAsInt();
        return Instant.ofEpochSecond(seconds, nanos);
    }

    @Override
    public JsonElement serialize(Instant src, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty(SECONDS_NAME, src.getEpochSecond());
        object.addProperty(NANOS_NAME, src.getNano());
        return object;
    }
}
