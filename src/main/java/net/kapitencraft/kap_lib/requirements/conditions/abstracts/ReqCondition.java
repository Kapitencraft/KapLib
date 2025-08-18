package net.kapitencraft.kap_lib.requirements.conditions.abstracts;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.io.serialization.IDataGenElement;
import net.kapitencraft.kap_lib.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public abstract class ReqCondition<T extends ReqCondition<T>> implements IDataGenElement<T> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ReqCondition<?>> STREAM_CODEC = ByteBufCodecs.registry(ExtraRegistries.Keys.REQ_CONDITIONS).dispatch(ReqCondition::getSerializer, DataPackSerializer::getStreamCodec);
    public static final Codec<ReqCondition<?>> CODEC = ExtraRegistries.REQUIREMENT_TYPES.byNameCodec().dispatch(ReqCondition::getSerializer, RegistrySerializer::codec);

    public static <T extends ReqCondition<T>> DataPackSerializer<T> createSerializer(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return IDataGenElement.createSerializer(codec, streamCodec);
    }

    //data-gen
    @SuppressWarnings("unchecked")
    public static <T extends ReqCondition<T>> ReqCondition<T> readFromJson(JsonObject object) {
        try {
            DataPackSerializer<T> serializer = (DataPackSerializer<T>) ExtraRegistries.REQUIREMENT_TYPES.get(ResourceLocation.parse(GsonHelper.getAsString(object, "type")));
            if (serializer == null)
                throw new NullPointerException("unknown requirement type: '" + GsonHelper.getAsString(object, "type") + "'");
            return Objects.requireNonNull(serializer.parseOrThrow(GsonHelper.getAsJsonObject(object, "data")));
        } catch (Exception e) {
             RequirementManager.LOGGER.warn(Markers.REQUIREMENTS_MANAGER, "error loading requirement: {}", e.getMessage());
            return null;
        }
    }

    private Component displayCache;

    protected ReqCondition() {
    }

    public final JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add("data", getSerializer().encode((T) this));
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