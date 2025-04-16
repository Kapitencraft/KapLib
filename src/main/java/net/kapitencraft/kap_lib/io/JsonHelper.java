package net.kapitencraft.kap_lib.io;

import com.google.gson.*;
import net.kapitencraft.kap_lib.io.serialization.ExtraJsonSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public interface JsonHelper {
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static Stream<JsonObject> castToObjects(JsonArray array) {
        return array.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject);
    }

    static <T> T getAsRegistryElement(JsonObject pObject, String pMemberName, IForgeRegistry<T> registry) {
        JsonElement pJson = pObject.get(pMemberName);
        if (pJson.isJsonPrimitive()) {
            String s = pJson.getAsString();
            return registry.getHolder(new ResourceLocation(s)).orElseThrow(() ->
                    new JsonSyntaxException("Expected " + pMemberName + " to be of " + registry.getRegistryName() + ", was unknown string '" + s + "'")
            ).get();
        } else {
            throw new JsonSyntaxException("Expected " + pMemberName + " to be of " + registry.getRegistryName() + ", was " + GsonHelper.getType(pJson));
        }
    }

    static <T> void addRegistryElement(JsonObject pJson, String name, T value, IForgeRegistry<T> registry) {
        pJson.addProperty(name, Objects.requireNonNull(registry.getKey(value), "unknown element in registry: " + value).toString());
    }

    static Vec3 getAsVec3(JsonObject pObject, String id) {
        return ExtraJsonSerializers.VEC_3.deserialize(pObject.get(id));
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

    static ItemStack getAsItemStack(JsonObject object) {
        Item item = JsonHelper.getAsRegistryElement(object, "item", ForgeRegistries.ITEMS);
        int count = GsonHelper.getAsInt(object, "count", 1);
        CompoundTag tag = CraftingHelper.getNBT(object.get("tag"));
        return new ItemStack(item, count, tag);
    }

    static void addItemStack(JsonObject pObject, String name, ItemStack stack) {
        JsonObject object = new JsonObject();
        addRegistryElement(object, "item", stack.getItem(), ForgeRegistries.ITEMS);
        if (stack.getCount() != 1) pObject.addProperty("count", stack.getCount());
        if (stack.getTag() != null) pObject.addProperty("tag", stack.getTag().toString());
        pObject.add(name, object);
    }
}
