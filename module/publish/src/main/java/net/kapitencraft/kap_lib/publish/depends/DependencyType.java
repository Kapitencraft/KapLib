package net.kapitencraft.kap_lib.publish.depends;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public enum DependencyType {
    REQUIRED("requiredDependency"),
    OPTIONAL("optionalDependency"),
    INCOMPATIBLE("incompatible"),
    EMBEDDED("embeddedLibrary");

    private final String curseforgeId;

    DependencyType(String curseforgeId) {
        this.curseforgeId = curseforgeId;
    }

    public String modrinthId() {
        return this.name().toLowerCase();
    }

    public String curseforgeId() {
        return curseforgeId;
    }

    public static class Deserializer implements JsonDeserializer<DependencyType> {

        @Override
        public DependencyType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString())
                throw new JsonParseException("dependency type must be string");
            try {
                return DependencyType.valueOf(json.getAsJsonPrimitive().getAsString().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("unknown dependency type: " + json.getAsJsonPrimitive().getAsString());
            }
        }
    }
}