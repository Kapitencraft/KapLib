package net.kapitencraft.kap_lib.publish.info;

import com.google.gson.*;
import net.kapitencraft.kap_lib.publish.depends.DependencyInfo;
import net.kapitencraft.kap_lib.publish.Environment;
import net.kapitencraft.kap_lib.publish.GsonHelper;

import java.lang.reflect.Type;

public record Config(AuthorInfo authorInfo,
                     ModInfo modInfo,
                     String mcVersion,
                     String loaderVersion,
                     String modrinthId,
                     String curseforgeId,
                     ArtifactInfo[] archives,
                     boolean withSources,
                     DependencyInfo[] dependencies,
                     ChangelogInfo changelogInfo,
                     AssetsInfo assetsInfo,
                     Environment environment) {

    public static class Deserializer implements JsonDeserializer<Config> {

        @Override
        public Config deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) throw new JsonParseException("mod info must be object");
            JsonObject object = jsonElement.getAsJsonObject();
            AuthorInfo authorInfo = jsonDeserializationContext.deserialize(object.get("author"), AuthorInfo.class);
            ModInfo modInfo = jsonDeserializationContext.deserialize(object.get("mod"), ModInfo.class);
            String mcVersion = GsonHelper.getAsString(object, "mc_version");
            String loaderVersion = GsonHelper.getAsString(object, "loader_version");
            String modrinthId = GsonHelper.getOptionalAsString(object, "modrinth_id");
            String curseforgeId = GsonHelper.getOptionalAsString(object, "curseforge_id");
            ArtifactInfo[] artifacts = object.has("artifacts") ? jsonDeserializationContext.deserialize(object.get("artifacts"), ArtifactInfo[].class) : new ArtifactInfo[0];
            boolean withSources = GsonHelper.getOptionalAsBoolean(object, "with_sources", false);
            DependencyInfo[] infos = jsonDeserializationContext.deserialize(object.get("dependencies"), DependencyInfo[].class);
            ChangelogInfo changelogInfo = object.has("changelog") ? jsonDeserializationContext.deserialize(object.get("changelog"), ChangelogInfo.class) : ChangelogInfo.DEFAULT;
            AssetsInfo assetsInfo = object.has("assets") ? jsonDeserializationContext.deserialize(object.get("assets"), AssetsInfo.class) : AssetsInfo.DEFAULT;
            Environment environment = object.has("environment") ? jsonDeserializationContext.deserialize(object.get("environment"), Environment.class) : Environment.CLIENT_AND_SERVER;
            return new Config(authorInfo, modInfo, mcVersion, loaderVersion, modrinthId, curseforgeId, artifacts, withSources, infos, changelogInfo, assetsInfo, environment);
        }
    }
}
