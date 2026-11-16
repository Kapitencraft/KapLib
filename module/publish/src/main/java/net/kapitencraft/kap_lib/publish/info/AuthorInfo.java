package net.kapitencraft.kap_lib.publish.info;

import com.google.gson.*;
import net.kapitencraft.kap_lib.publish.GsonHelper;

import java.lang.reflect.Type;

public record AuthorInfo(String email, String name) {

    public static class Deserializer implements JsonDeserializer<AuthorInfo> {

        @Override
        public AuthorInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) throw new JsonParseException("author config must be object");
            JsonObject object = jsonElement.getAsJsonObject();
            String email = GsonHelper.getAsString(object, "email");
            String name = GsonHelper.getAsString(object, "name");
            return new AuthorInfo(email, name);
        }
    }
}