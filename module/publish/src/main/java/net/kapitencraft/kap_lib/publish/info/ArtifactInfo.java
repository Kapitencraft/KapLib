package net.kapitencraft.kap_lib.publish.info;

import com.google.gson.*;
import net.kapitencraft.kap_lib.publish.GsonHelper;

import java.lang.reflect.Type;

public record ArtifactInfo(String sourcePath, String name) {

    public static class Deserializer implements JsonDeserializer<ArtifactInfo> {

        @Override
        public ArtifactInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) throw new JsonParseException("mod info must be object");
            JsonObject object = json.getAsJsonObject();
            String sourcePath = GsonHelper.getAsString(object, "artifactPath");
            String artifactName = GsonHelper.getAsString(object, "artifactName");
            return new ArtifactInfo(sourcePath, artifactName);
        }
    }
}
