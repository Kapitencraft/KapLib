package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public class DataPackSerializer<T> extends JsonSerializer<T> {
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public DataPackSerializer(Codec<T> codec, Supplier<T> defaulted, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        super(codec, defaulted);
        this.streamCodec = streamCodec;
    }

    public DataPackSerializer(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        super(codec);
        this.streamCodec = streamCodec;
    }

    public static <T> DataPackSerializer<T> unit(Supplier<T> sup) {
        return new DataPackSerializer<>(Codec.unit(sup), sup, StreamCodec.unit(sup.get()));
    }

    public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
        return streamCodec;
    }
}
