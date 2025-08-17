package net.kapitencraft.kap_lib.io;

import com.google.gson.*;
import net.kapitencraft.kap_lib.io.serialization.ExtraJsonSerializers;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface JsonHelper {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static Stream<JsonObject> castToObjects(JsonArray array) {
        return array.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject);
    }

    static Vec3 getAsVec3(JsonObject pObject, String id) {
        return ExtraJsonSerializers.VEC_3.parse(pObject.get(id));
    }

    static @Nullable Boolean getAsOptionalBool(JsonObject pObject, String name) {
        return pObject.has(name) ? GsonHelper.getAsBoolean(pObject, name) : null;
    }

    static @Nullable Integer getAsOptionalInt(JsonObject pObject, String name) {
        return pObject.has(name) ? GsonHelper.getAsInt(pObject, name) : null;
    }


    static void addOptionalBool(JsonObject pJson, String name, Boolean val) {
        if (val != null) pJson.addProperty(name, val);
    }

    static void addOptionalInt(JsonObject pJson, String name, Integer val) {
        if (val != null) pJson.addProperty(name, val);
    }
}
