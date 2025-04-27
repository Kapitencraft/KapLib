package net.kapitencraft.kap_lib.requirements.conditions.abstracts;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.io.serialization.IDataGenElement;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ReqCondition<T extends ReqCondition<T>> implements IDataGenElement<T> {
    public static <T extends ReqCondition<T>> DataPackSerializer<T> createSerializer(Codec<T> codec, FriendlyByteBuf.Reader<T> reader) {
        return IDataGenElement.createSerializer(codec, reader);
    }

    public static <T extends ReqCondition<T>> ReqCondition<T> fromNetwork(FriendlyByteBuf buf) {
        return IDataGenElement.fromNetwork(buf);
    }
    //data-gen
    public static <T extends ReqCondition<T>> ReqCondition<T> readFromJson(JsonObject object) {
        DataPackSerializer<T> serializer = (DataPackSerializer<T>) ExtraRegistries.REQUIREMENT_TYPES.getValue(new ResourceLocation(GsonHelper.getAsString(object, "type")));
        if (serializer == null) throw new NullPointerException("unknown requirement type: '" + GsonHelper.getAsString(object, "type") + "'");
        return serializer.deserialize(GsonHelper.getAsJsonObject(object, "data"));
    }

    private Component displayCache;

    protected ReqCondition() {
    }

    //network
    public final void toNetwork(FriendlyByteBuf byteBuf) {
        byteBuf.writeRegistryId(ExtraRegistries.REQUIREMENT_TYPES, this.getSerializer());
        this.getSerializer().toNetwork(byteBuf, (T) this);
    }

    public final JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add("data", getSerializer().serialize((T) this));
        object.addProperty("type", Objects.requireNonNull(ExtraRegistries.REQUIREMENT_TYPES.getKey(this.getSerializer()), String.format("unknown requirement type: %s", this.getClass().getCanonicalName())).toString());
        return object;
    }

    public abstract boolean matches(LivingEntity player);

    public abstract DataPackSerializer<T> getSerializer();

    protected abstract @NotNull Component cacheDisplay();

    public @NotNull Component display() {
        return displayCache == null ? displayCache = cacheDisplay() : displayCache;
    }

}