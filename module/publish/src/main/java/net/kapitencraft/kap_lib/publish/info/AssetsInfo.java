package net.kapitencraft.kap_lib.publish.info;

import com.google.gson.*;
import net.kapitencraft.kap_lib.publish.GsonHelper;

import java.lang.reflect.Type;

public record AssetsInfo(String sourcePath, String changelogPath, String categoriesPath) {
    static final String SOURCE_PATH = "build/libs";
    static final String CHANGELOG_PATH = "publish/changelog.txt";
    static final String CATEGORIES_PATH = "publish/categories.json";

    public static final AssetsInfo DEFAULT = new AssetsInfo(SOURCE_PATH, CHANGELOG_PATH, CATEGORIES_PATH);

    public static class Deserializer implements JsonDeserializer<AssetsInfo> {

        @Override
        public AssetsInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) throw new JsonParseException("mod info must be object");
            JsonObject object = jsonElement.getAsJsonObject();
            String sourcePath = GsonHelper.getOptionalAsString(object, "source", SOURCE_PATH);
            String changelogPath = GsonHelper.getOptionalAsString(object, "changelog", CHANGELOG_PATH);
            String categoriesPath = GsonHelper.getOptionalAsString(object, "categories", CATEGORIES_PATH);
            return new AssetsInfo(sourcePath, changelogPath, categoriesPath);
        }
    }
}