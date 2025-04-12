package net.kapitencraft.kap_lib.io;

import com.google.gson.*;
import net.kapitencraft.kap_lib.io.serialization.ExtraJsonSerializers;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.stream.Stream;

public interface JsonHelper {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static Stream<JsonObject> castToObjects(JsonArray array) {
        return array.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject);
    }

    static <T> T convertToRegistryElement(JsonObject pObject, String pMemberName, IForgeRegistry<T> registry) {
        JsonElement pJson = pObject.get(pMemberName);
        if (pJson.isJsonPrimitive()) {
            String s = pJson.getAsString();
            return registry.getHolder(new ResourceLocation(s)).orElseThrow(() ->
                    new JsonSyntaxException("Expected " + pMemberName + " to be an item, was unknown string '" + s + "'")
            ).get();
        } else {
            throw new JsonSyntaxException("Expected " + pMemberName + " to be an item, was " + GsonHelper.getType(pJson));
        }
    }

    static Vec3 getAsVec3(JsonObject pObject, String id) {
        return ExtraJsonSerializers.VEC_3.deserialize(pObject.get(id));
    }
}
