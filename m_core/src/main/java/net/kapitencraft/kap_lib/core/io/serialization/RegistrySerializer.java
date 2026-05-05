package net.kapitencraft.kap_lib.core.io.serialization;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

/**
 * serializer for json-based registry content. see parameters for more info
 * @param codec the codec. use inside {@link com.mojang.serialization.Codec#dispatch Codec#dispatch}
 * @param streamCodec the stream codec. use inside {@link StreamCodec#dispatch(Function, Function) }
 * @param <L> the type of the serializer
 */
public record RegistrySerializer<L>(MapCodec<L> codec, StreamCodec<RegistryFriendlyByteBuf, L> streamCodec) {

    /**
     * creates a registry serializer that always returns the given value
     * @param value the value to use in the serializer
     * @param <L> the value type
     * @return a registry serializer with the given value of the given type
     */
    public static <L> RegistrySerializer<L> unit(L value) {
        return new RegistrySerializer<>(MapCodec.unit(value), StreamCodec.unit(value));
    }
}
