package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import org.checkerframework.checker.units.qual.K;

import java.util.HashMap;
import java.util.Map;

public class BoundMapCodec<K, V> implements Codec<Map<K, V>> {
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    public BoundMapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
    }

    @Override
    public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getMap(input).flatMap(tMapLike -> {
            Map<K, V> map = new HashMap<>();

            StringBuilder exception = new StringBuilder();
            tMapLike.entries().forEach(ttPair -> {
                DataResult<K> key = keyCodec.parse(ops, ttPair.getFirst());
                DataResult<V> value = valueCodec.parse(ops, ttPair.getSecond());
                key.get().ifLeft(
                        k -> value.get().ifLeft(
                                v -> map.put(k, v)
                        ).ifRight(partial -> {
                            exception.append(" value error: ");
                            exception.append(partial.message());
                        })
                ).ifRight(partial -> {
                    exception.append(" key error: ");
                    exception.append(partial.message());
                });
            });
            if (!exception.isEmpty()) return DataResult.error(exception::toString);
            return DataResult.success(Pair.of(map, input));
        });
    }

    @Override
    public <T> DataResult<T> encode(Map<K, V> input, DynamicOps<T> ops, T prefix) {
        RecordBuilder<T> builder = ops.mapBuilder();
        input.forEach((k, v) -> builder.add(keyCodec.encodeStart(ops, k), valueCodec.encodeStart(ops, v)));
        return builder.build(prefix);
    }
}
