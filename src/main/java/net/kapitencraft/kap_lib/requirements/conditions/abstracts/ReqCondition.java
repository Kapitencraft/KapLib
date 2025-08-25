package net.kapitencraft.kap_lib.requirements.conditions.abstracts;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class ReqCondition<T extends ReqCondition<T>> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ReqCondition<?>> STREAM_CODEC = ByteBufCodecs.registry(ExtraRegistries.Keys.REQ_CONDITIONS).dispatch(ReqCondition::getSerializer, RegistrySerializer::streamCodec);
    public static final Codec<ReqCondition<?>> CODEC = ExtraRegistries.REQUIREMENT_TYPES.byNameCodec().dispatch(ReqCondition::getSerializer, RegistrySerializer::codec);

    private Component displayCache;

    protected ReqCondition() {
    }

    public abstract boolean matches(LivingEntity player);

    public abstract RegistrySerializer<T> getSerializer();

    protected abstract @NotNull Component cacheDisplay();

    public @NotNull Component display() {
        return displayCache == null ? displayCache = cacheDisplay() : displayCache;
    }

}