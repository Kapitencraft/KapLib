package net.kapitencraft.kap_lib.collection;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.stream.Consumers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * a stream for a map (how obvious)
 */
public class MapStream<K, V> {
    private final Map<K, V> map = new HashMap<>();

    public static <T, K> MapStream<T, K> of(Map<T, K> map) {
        MapStream<T, K> stream = new MapStream<>();
        stream.map.putAll(map);
        return stream;
    }

    public static <T, K> MapStream<T, K> create() {
        return new MapStream<>();
    }

    public static <T, K> MapStream<T, K> create(List<T> keys, List<K> values) {
        if (keys.size() != values.size()) {
            throw new IllegalStateException("tried creating map from different length collections");
        }
        List<StreamEntry<T, K>> entries = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            entries.add(new StreamEntry<>(keys.get(i), values.get(i)));
        }
        return of(entries);
    }


    public <J> Stream<J> mapToSimple(BiFunction<K, V, J> mapper) {
        return collect(BiCollectors.toStream(mapper));
    }

    /**
     * method to remove any elements that do <i>not</i> match the predicate
     * @return the filtered {@link MapStream}
     */
    public MapStream<K, V> filter(BiPredicate<K, V> predicate) {
        Map<K, V> map = new HashMap<>();
        this.map.forEach((k, v) -> {
            if (predicate.test(k, v)) {
                map.put(k, v);
            }
        });
        return of(map);
    }

    public MapStream<K, V> filterKeys(Predicate<K> keyFilter) {
        return filterKeys(keyFilter, null);
    }

    public MapStream<K, V> filterKeys(Predicate<K> keyFilter, @Nullable BiConsumer<K, V> forFailed) {
        Map<K, V> map = new HashMap<>();
        this.map.forEach((k, v) -> {
            if (keyFilter.test(k))
                map.put(k, v);
            else if (forFailed != null) forFailed.accept(k, v);
        });
        return of(map);

    }

    public MapStream<K, V> filterValues(Predicate<V> keyFilter, @Nullable BiConsumer<K, V> forFailed) {
        Map<K, V> map = new HashMap<>();
        this.map.forEach((k, v) -> {
            if (keyFilter.test(v))
                map.put(k, v);
            else if (forFailed != null) forFailed.accept(k, v);
        });
        return of(map);
    }


    public <J> MapStream<K, J> mapValues(Function<V, J> mapper) {
        List<K> keys = this.map.keySet().stream().toList();
        List<J> values = this.map.values().stream().map(mapper).toList();
        return create(keys, values);
    }

    public <J> MapStream<J, V> mapKeys(Function<K, J> mapper) {
        List<J> keys = this.map.keySet().stream().map(mapper).toList();
        List<V> values = this.map.values().stream().toList();
        return create(keys, values);
    }

    public MapStream<K, V> filterNulls() {
        return filter((k, v) -> k != null && v != null);
    }

    public Map<K, V> toMap() {
        return Map.copyOf(this.map);
    }

    public <J, I> MapStream<J, I> biMap(BiFunction<K, V, StreamEntry<J, I>> mapper) {
        return of(mapToSimple(mapper).toList());
    }

    public MapStream<K, V> forEach(BiConsumer<K, V> consumer) {
        this.map.forEach(consumer);
        return this;
    }

    private static <T, K> MapStream<T, K> of(List<StreamEntry<T, K>> list) {
        Map<T, K> map = new HashMap<>();
        list.forEach(entry -> {
            map.put(entry.t(), entry.k());
        });
        return of(map);
    }

    public boolean allMatch(BiPredicate<K, V> predicate) {
        int i = map.size();
        MapStream<K, V> filtered = filter(predicate);
        return i == filtered.map.size();
    }

    public boolean anyMatch(BiPredicate<K, V> predicate) {
        return !filter(predicate).map.isEmpty();
    }

    public boolean noneMatch(BiPredicate<K, V> predicate) {
        return filter(predicate).map.isEmpty();
    }

    public <A, R> R collect(BiCollector<? super K, ? super V, A, R> collector) {
        A arg = collector.supplier().get();
        this.forEach((k, v) -> collector.accumulator().apply(arg, k, v));
        return collector.finisher().apply(arg);
    }

    public Pair<K, V>[] toPairArray() {
        return this.mapToSimple(Pair::new).toArray(Pair[]::new);
    }

    public List<Pair<K, V>> toPairList() {
        return this.mapToSimple(Pair::new).toList();
    }
}
