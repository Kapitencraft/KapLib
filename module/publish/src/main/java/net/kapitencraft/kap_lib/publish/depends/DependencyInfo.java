package net.kapitencraft.kap_lib.publish.depends;

import com.google.gson.*;
import net.kapitencraft.kap_lib.publish.GsonHelper;
import net.kapitencraft.kap_lib.publish.ModrinthPublish;

import java.io.IOException;
import java.lang.reflect.Type;

public record DependencyInfo(String modrinthId, String curseforgeId, String versionName, DependencyType type,
                             int ordinal) {

    public JsonObject toModrinthDependency(String gameVersion) throws IOException {
        JsonObject object = new JsonObject();
        object.addProperty("version_id", ModrinthPublish.getDependencyVersionId(modrinthId, gameVersion, versionName, ordinal));
        object.addProperty("project_id", modrinthId);
        object.addProperty("dependency_type", type.modrinthId());
        return object;
    }

    public JsonObject toCurseforgeDependency() {
        JsonObject object = new JsonObject();
        object.addProperty("slug", curseforgeId);
        object.addProperty("type", this.type.curseforgeId());
        return object;
    }

    public static class Deserializer implements JsonDeserializer<DependencyInfo> {

        @Override
        public DependencyInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) throw new JsonParseException("dependency info must be object");
            JsonObject object = jsonElement.getAsJsonObject();
            String modrinthId = GsonHelper.getOptionalAsString(object, "modrinth_id");
            String curseforgeId = GsonHelper.getOptionalAsString(object, "curseforge_id");
            String versionName = GsonHelper.getAsString(object, "version_name");
            DependencyType depType = jsonDeserializationContext.deserialize(object.get("type"), DependencyType.class);
            int ordinal = GsonHelper.getOptionalAsInt(object, "ordinal", 0);
            return new DependencyInfo(modrinthId, curseforgeId, versionName, depType, ordinal);
        }
    }
}
