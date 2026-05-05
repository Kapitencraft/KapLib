package net.kapitencraft.kap_lib.core.io.serialization;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

/**
 * serializer for both network and disc
 * @param <T> type to serialize
 */
public class DataPackSerializer<T> extends JsonSerializer<T> {
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

    public DataPackSerializer(Codec<T> codec, Supplier<T> defaulted, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        super(codec, defaulted);
        this.streamCodec = streamCodec;
    }

    public DataPackSerializer(Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        super(codec);
        this.streamCodec = streamCodec;
    }

    public static <T> DataPackSerializer<T> unit(Supplier<T> sup) {
        return new DataPackSerializer<>(Codec.unit(sup), sup, StreamCodec.unit(sup.get()));
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, T> getStreamCodec() {
        return streamCodec;
    }
}
