package net.kapitencraft.kap_lib.io.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.util.function.Supplier;

public class JsonSerializer<T> extends Serializer<JsonElement, JsonOps, T> {
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
}
