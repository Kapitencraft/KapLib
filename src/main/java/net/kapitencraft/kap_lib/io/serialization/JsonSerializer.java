package net.kapitencraft.kap_lib.io.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class JsonSerializer<T> extends Serializer<JsonElement, JsonOps, T> implements net.minecraft.world.level.storage.loot.Serializer<T> {
    public JsonSerializer(Codec<T> codec, Supplier<T> defaulted) {
        super(JsonOps.INSTANCE, codec, defaulted);
    }

    public JsonSerializer(Codec<T> codec) {
        this(codec, null);
    }

    @Override
    JsonObject getSerializeDefault() {
        return new JsonObject();
    }

    @Override
    public void serialize(@NotNull JsonObject jsonObject, @NotNull T t, @NotNull JsonSerializationContext context) {
        JsonObject object = (JsonObject) encode(t);
        object.asMap().forEach(jsonObject::add);
    }

    @Override
    public @NotNull T deserialize(@NotNull JsonObject object, @NotNull JsonDeserializationContext context) {
        return parse(object);
    }
}
