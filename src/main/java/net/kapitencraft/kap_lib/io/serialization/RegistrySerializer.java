package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

/**
 * @param codec the codec. use inside {@link com.mojang.serialization.Codec#dispatch Codec#dispatch}
 * @param streamCodec the stream codec. use inside {@link StreamCodec#dispatch(Function, Function) }
 * @param <L> the type of the serializer
 */
public record RegistrySerializer<L>(MapCodec<L> codec, StreamCodec<RegistryFriendlyByteBuf, L> streamCodec) {
}
