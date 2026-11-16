package net.kapitencraft.kap_lib.publish.info;

import com.google.gson.*;
import net.kapitencraft.kap_lib.publish.GsonHelper;

import java.lang.reflect.Type;

public record ModInfo(String id, String name, String version, String artifactVersion) {

    public static class Deserializer implements JsonDeserializer<ModInfo> {

        @Override
        public ModInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) throw new JsonParseException("mod info must be object");
            JsonObject object = jsonElement.getAsJsonObject();
            String id = GsonHelper.getAsString(object, "id");
            String name = GsonHelper.getAsString(object, "name");
            String version = GsonHelper.getAsString(object, "version");
            String artifactVersion = GsonHelper.getAsString(object, "artifact_version");
            return new ModInfo(id, name, version, artifactVersion);
        }
    }
}
