package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Supplier;

public class NullableCodec<T> implements Codec<T> {
    private final Codec<T> codec;
    private final Supplier<T> fallback;

    public NullableCodec(Codec<T> codec, Supplier<T> fallback) {
        this.codec = codec;
        this.fallback = fallback;
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        if (ops.empty() == input) return DataResult.success(Pair.of(fallback.get(), input));
        return codec.decode(ops, input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        if (input == null) return DataResult.success(ops.empty());
        return codec.encode(input, ops, prefix);
    }

    @Override
    public String toString() {
        return "Nullable[" + codec + "]";
    }
}
