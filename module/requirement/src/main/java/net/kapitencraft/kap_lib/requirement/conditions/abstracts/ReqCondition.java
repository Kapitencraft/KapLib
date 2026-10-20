package net.kapitencraft.kap_lib.requirement.conditions.abstracts;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.requirement.registry.RequirementRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class ReqCondition<T extends ReqCondition<T>> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ReqCondition<?>> STREAM_CODEC = ByteBufCodecs.registry(RequirementRegistries.Keys.REQ_CONDITIONS).dispatch(ReqCondition::getSerializer, RegistrySerializer::streamCodec);
    public static final Codec<ReqCondition<?>> CODEC = RequirementRegistries.REQUIREMENT_TYPES.byNameCodec().dispatch(ReqCondition::getSerializer, RegistrySerializer::codec);

    private Component displayCache;

    protected ReqCondition() {}

    public abstract boolean matches(LivingEntity player);

    public abstract RegistrySerializer<T> getSerializer();

    protected abstract @NotNull Component cacheDisplay();

    public @NotNull Component display() {
        return displayCache == null ? displayCache = cacheDisplay() : displayCache;
    }

}