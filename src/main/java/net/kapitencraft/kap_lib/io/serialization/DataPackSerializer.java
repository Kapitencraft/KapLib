package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class DataPackSerializer<T> extends JsonSerializer<T> {
    private final FriendlyByteBuf.Reader<T> reader;
    private final FriendlyByteBuf.Writer<T> writer;

    public DataPackSerializer(Codec<T> codec, Supplier<T> defaulted, FriendlyByteBuf.Reader<T> reader, FriendlyByteBuf.Writer<T> writer) {
        super(codec, defaulted);
        this.reader = reader;
        this.writer = writer;
    }

    public DataPackSerializer(Codec<T> codec, FriendlyByteBuf.Reader<T> reader, FriendlyByteBuf.Writer<T> writer) {
        super(codec);
        this.reader = reader;
        this.writer = writer;
    }

    public void toNetwork(FriendlyByteBuf buf, T value) {
        writer.accept(buf, value);
    }

    public T fromNetwork(FriendlyByteBuf buf) {
        return reader.apply(buf);
    }
}
