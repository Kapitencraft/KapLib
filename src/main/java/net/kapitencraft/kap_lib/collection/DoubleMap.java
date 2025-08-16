package net.kapitencraft.kap_lib.collection;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.stream.Consumers;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * class that contains a map of a map making it able to contain 3 different types
 */
public class DoubleMap<MK, K, V> extends HashMap<MK, HashMap<K, V>> {
    public static <MK, K, V> Codec<DoubleMap<MK, K, V>> createCodec(Codec<MK> mKeyCodec, Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.unboundedMap(mKeyCodec, Codec.unboundedMap(keyCodec, valueCodec)).xmap(DoubleMap::of, Function.identity());
    }

    /**
     * used to make a double map immutable, throwing an exception when trying to modify it
     */
    private boolean immutable = false;

    public void forValues(Consumer<V> consumer) {
        this.values().stream().map(Map::values).flatMap(Collection::stream).forEach(consumer);
    }

    /**
     * @return a new DoubleMap containing {@code map} elements
     */
    public static <T, K, L, M extends Map<T, ? extends Map<K, L>>> DoubleMap<T, K, L> of(M map) {
        DoubleMap<T, K, L> map1 = DoubleMap.create();
        map1.putAll(map);
        return map1;
    }

    /**
     * gets or creates a value associated with this key
     */
    public Map<K, V> getOrCreate(MK element) {
        if (get(element) == null) {
            this.put(element, new HashMap<>());
        }
        return get(element);
    }

    /**
     * @return the values of the values of this map
     */
    public Collection<V> actualValues() {
        return CollectionHelper.values(this);
    }

    /**
     */
    @Override
    public Map<K, V> put(MK key, Map<K, V> value) {
        if (this.immutable) throw new UnsupportedOperationException("tried modifying immutable double map");
        return super.put(key, value);
    }

    /**
     * @return this
     * makes this DoubleMap un-modifiable
     */
    public DoubleMap<MK, K, V> immutable() {
        this.immutable = true;
        return this;
    }


    /**
     * applies the consumer to all values in this Map
     */
    public void forMap(BiConsumer<K, V> biConsumer) {
        this.values().forEach(map -> map.forEach(biConsumer));
    }

    /**
     * @return a new DoubleMap
     */
    @Contract(value = " -> new", pure = true)
    public static <T, K, L> @NotNull DoubleMap<T, K, L> create() {
        return new DoubleMap<>();
    }

    /**
     * adds new elements to this map
     */
    public void put(MK MK, K k, V v) {
        if (this.immutable) throw new UnsupportedOperationException("tried modifying immutable double map");
        if (this.containsKey(MK)) {
            this.get(MK).put(k, v);
        } else {
            HashMap<K, V> map = new HashMap<>();
            map.put(k, v);
            this.put(MK, map);
        }
    }

    /**
     * gets or adds elements to this map
     */
    public V putIfAbsent(MK mk, K k, V ifAbsent) {
        if (this.immutable) throw new UnsupportedOperationException("tried modifying immutable double map");
        if (this.containsKey(mk)) {
            this.get(mk).putIfAbsent(k, ifAbsent);
        } else {
            this.put(mk, new HashMap<>());
            this.get(mk).put(k, ifAbsent);
        }
        return get(mk, k);
    }
    public V get(MK MK, K k) {
        if (!containsKey(MK)) return null;
        return get(MK).get(k);
    }

    public V computeIfAbsent(MK mk, K k, BiFunction<MK, K, V> mapper) {
        return putIfAbsent(mk, k, mapper.apply(mk, k));
    }

    /**
     * loops threw all elements in this map
     */
    public void forAllEach(Consumers.C3<MK, K, V> consumer) {
        this.forEach((mk, kvMap) -> kvMap.forEach((k, v) -> consumer.apply(mk, k, v)));
    }
}