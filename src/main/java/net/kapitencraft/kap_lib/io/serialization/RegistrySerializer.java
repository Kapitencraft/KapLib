package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param codec the codec. use inside {@link com.mojang.serialization.Codec#dispatch Codec#dispatch}
 * @param streamCodec the stream codec. use inside {@link StreamCodec#dispatch(Function, Function) }
 * @param <L> the type of the serializer
 */
public record RegistrySerializer<L>(MapCodec<L> codec, StreamCodec<RegistryFriendlyByteBuf, L> streamCodec) {

    /**
     * @param value the value to use in the serializer
     * @param <L> the value type
     * @return a registry serializer with the given value of the given type
     */
    public static <L> RegistrySerializer<L> unit(L value) {
        return new RegistrySerializer<>(MapCodec.unit(value), StreamCodec.unit(value));
    }
}
