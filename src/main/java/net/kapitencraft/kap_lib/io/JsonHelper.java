package net.kapitencraft.kap_lib.io;

import com.google.gson.*;

import java.util.stream.Stream;

public interface JsonHelper {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static Stream<JsonObject> castToObjects(JsonArray array) {
        return array.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject);
    }
}
