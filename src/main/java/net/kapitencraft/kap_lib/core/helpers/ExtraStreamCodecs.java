package net.kapitencraft.kap_lib.core.helpers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.core.collection.DoubleMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class ExtraStreamCodecs {

    /**
     * composite stream codec for 7 elements
     */
    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> getter1,
            StreamCodec<? super B, T2> codec2, Function<C, T2> getter2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> getter3,
            StreamCodec<? super B, T4> codec4, Function<C, T4> getter4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> getter5,
            StreamCodec<? super B, T6> codec6, Function<C, T6> getter6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> getter7,
            Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B buffer, C value) {
                codec1.encode(buffer, getter1.apply(value));
                codec2.encode(buffer, getter2.apply(value));
                codec3.encode(buffer, getter3.apply(value));
                codec4.encode(buffer, getter4.apply(value));
                codec5.encode(buffer, getter5.apply(value));
                codec6.encode(buffer, getter6.apply(value));
                codec7.encode(buffer, getter7.apply(value));
            }
        };
    }

    /**
     * composite stream codec for 8 elements
     */
    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> getter1,
            StreamCodec<? super B, T2> codec2, Function<C, T2> getter2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> getter3,
            StreamCodec<? super B, T4> codec4, Function<C, T4> getter4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> getter5,
            StreamCodec<? super B, T6> codec6, Function<C, T6> getter6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> getter7,
            StreamCodec<? super B, T8> codec8, Function<C, T8> getter8,
            Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            @Override
            public void encode(B buffer, C value) {
                codec1.encode(buffer, getter1.apply(value));
                codec2.encode(buffer, getter2.apply(value));
                codec3.encode(buffer, getter3.apply(value));
                codec4.encode(buffer, getter4.apply(value));
                codec5.encode(buffer, getter5.apply(value));
                codec6.encode(buffer, getter6.apply(value));
                codec7.encode(buffer, getter7.apply(value));
                codec8.encode(buffer, getter8.apply(value));
            }
        };
    }

    /**
     * composite stream codec for 9 elements
     */
    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> getter1,
            StreamCodec<? super B, T2> codec2, Function<C, T2> getter2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> getter3,
            StreamCodec<? super B, T4> codec4, Function<C, T4> getter4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> getter5,
            StreamCodec<? super B, T6> codec6, Function<C, T6> getter6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> getter7,
            StreamCodec<? super B, T8> codec8, Function<C, T8> getter8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> getter9,
            Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                T9 t9 = codec9.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            }

            @Override
            public void encode(B buffer, C value) {
                codec1.encode(buffer, getter1.apply(value));
                codec2.encode(buffer, getter2.apply(value));
                codec3.encode(buffer, getter3.apply(value));
                codec4.encode(buffer, getter4.apply(value));
                codec5.encode(buffer, getter5.apply(value));
                codec6.encode(buffer, getter6.apply(value));
                codec7.encode(buffer, getter7.apply(value));
                codec8.encode(buffer, getter8.apply(value));
                codec9.encode(buffer, getter9.apply(value));
            }
        };
    }

    /**
     * composite stream codec for 10 elements
     */
    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> StreamCodec<B, C> composite(
            StreamCodec<? super B, T1> codec1, Function<C, T1> getter1,
            StreamCodec<? super B, T2> codec2, Function<C, T2> getter2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> getter3,
            StreamCodec<? super B, T4> codec4, Function<C, T4> getter4,
            StreamCodec<? super B, T5> codec5, Function<C, T5> getter5,
            StreamCodec<? super B, T6> codec6, Function<C, T6> getter6,
            StreamCodec<? super B, T7> codec7, Function<C, T7> getter7,
            StreamCodec<? super B, T8> codec8, Function<C, T8> getter8,
            StreamCodec<? super B, T9> codec9, Function<C, T9> getter9,
            StreamCodec<? super B, T10> codec10, Function<C, T10> getter10,
            Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> factory
    ) {
        return new StreamCodec<>() {
            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 t1 = codec1.decode(buffer);
                T2 t2 = codec2.decode(buffer);
                T3 t3 = codec3.decode(buffer);
                T4 t4 = codec4.decode(buffer);
                T5 t5 = codec5.decode(buffer);
                T6 t6 = codec6.decode(buffer);
                T7 t7 = codec7.decode(buffer);
                T8 t8 = codec8.decode(buffer);
                T9 t9 = codec9.decode(buffer);
                T10 t10 = codec10.decode(buffer);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);
            }

            @Override
            public void encode(B buffer, C value) {
                codec1.encode(buffer, getter1.apply(value));
                codec2.encode(buffer, getter2.apply(value));
                codec3.encode(buffer, getter3.apply(value));
                codec4.encode(buffer, getter4.apply(value));
                codec5.encode(buffer, getter5.apply(value));
                codec6.encode(buffer, getter6.apply(value));
                codec7.encode(buffer, getter7.apply(value));
                codec8.encode(buffer, getter8.apply(value));
                codec9.encode(buffer, getter9.apply(value));
                codec10.encode(buffer, getter10.apply(value));
            }
        };
    }

    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> enumCodec(E[] elements) {
        return ByteBufCodecs.idMapper(v -> elements[v], Enum::ordinal);
    }

    public static final StreamCodec<? super FriendlyByteBuf, Vec3> VEC_3 = StreamCodec.of(FriendlyByteBuf::writeVec3, FriendlyByteBuf::readVec3);

    public static final StreamCodec<? super ByteBuf, EquipmentSlot> EQUIPMENT_SLOT = ByteBufCodecs.STRING_UTF8.map(EquipmentSlot::byName, EquipmentSlot::getName);

    public static final StreamCodec<ByteBuf, UUID> UUID = ByteBufCodecs.STRING_UTF8.map(java.util.UUID::fromString, java.util.UUID::toString);

    public static <R> StreamCodec<ByteBuf, TagKey<R>> tagKey(ResourceKey<Registry<R>> key) {
        return ResourceLocation.STREAM_CODEC.map(r -> TagKey.create(key, r), TagKey::location);
    }

    public static <B extends ByteBuf, V, K> StreamCodec.CodecOperation<B, V, Map<K, V>> map(StreamCodec<? super B, K> keyCodec) {
        return p_320272_ -> ByteBufCodecs.map(HashMap::new, keyCodec, p_320272_);
    }

    public static <B extends ByteBuf, K, V> StreamCodec<B, Multimap<K, V>> multimap(StreamCodec<? super B, K> keyCodec, StreamCodec<? super B, V> valueCodec) {
        return new StreamCodec<>() {
            @Override
            public Multimap<K, V> decode(B buffer) {
                int size = buffer.readInt();
                Multimap<K, V> map = HashMultimap.create();
                for (int i = 0; i < size; i++) {
                    K key = keyCodec.decode(buffer);
                    int vSize = buffer.readInt();
                    List<V> values = new ArrayList<>();
                    for (int j = 0; j < vSize; j++) {
                        values.add(valueCodec.decode(buffer));
                        map.putAll(key, values);
                    }
                }
                return map;
            }

            @Override
            public void encode(B buffer, Multimap<K, V> value) {
                Set<K> keys = value.keySet();
                buffer.writeInt(keys.size());
                for (K k : keys) {
                    keyCodec.encode(buffer, k);
                    Collection<V> values = value.get(k);
                    buffer.writeInt(value.size());
                    values.forEach(v -> valueCodec.encode(buffer, v));
                }
            }
        };
    }

    public static <B extends ByteBuf, MK, K, V> StreamCodec<B, DoubleMap<MK, K, V>> doubleMap(StreamCodec<? super B, MK> key1Codec, StreamCodec<? super B, K> key2Codec, StreamCodec<? super B, V> valueCodec) {
        return new StreamCodec<>() {
            @Override
            public DoubleMap<MK, K, V> decode(B buffer) {
                int size = buffer.readInt();
                DoubleMap<MK, K, V> map = new DoubleMap<>();
                for (int i = 0; i < size; i++) {
                    MK mk = key1Codec.decode(buffer);
                    int size1 = buffer.readInt();
                    Map<K, V> entry = new HashMap<>();
                    for (int i1 = 0; i1 < size1; i1++) {
                        entry.put(key2Codec.decode(buffer), valueCodec.decode(buffer));
                    }
                    map.put(mk, entry);
                }
                return map;
            }

            @Override
            public void encode(B buffer, DoubleMap<MK, K, V> value) {
                Set<MK> keys = value.keySet();
                buffer.writeInt(keys.size());
                for (MK key : keys) {
                    key1Codec.encode(buffer, key);
                    Map<K, V> map = value.get(key);
                    buffer.writeInt(map.size());
                    map.forEach((k, v) -> {
                        key2Codec.encode(buffer, k);
                        valueCodec.encode(buffer, v);
                    });
                }
            }
        };
    }
}
