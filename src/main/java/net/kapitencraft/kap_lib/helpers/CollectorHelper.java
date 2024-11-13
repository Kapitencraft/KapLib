package net.kapitencraft.kap_lib.helpers;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface CollectorHelper {
    /**
     * collect to a map with identity keys and {@code valueMapper} mapped values
     */
    static <T, L> Collector<T, ?, Map<T, L>> createMap(Function<T, L> valueMapper) {
        return Collectors.toMap(Function.identity(), valueMapper);
    }

    /**
     * collect to a map with identity values and {@code keyMapper} mapped keys
     */
    static <T, L> Collector<L, ?, Map<T, L>> createMapForKeys(Function<L, T> keyMapper) {
        return Collectors.toMap(keyMapper, Function.identity());
    }

    static <K, V, L> Collector<L, ?, List<Pair<K, V>>> toPairList(Function<L, K> keyMapper, Function<L, V> valueMapper) {
        return Collector.of(
                ArrayList::new,
                (pairs, l) -> pairs.add(new Pair<>(keyMapper.apply(l), valueMapper.apply(l))),
                (pairs, pairs2) -> {
                    pairs.addAll(pairs2);
                    return pairs;
                }
        );
    }

    /**
     * @param filler the Component to put between each of the other elements
     * @return a Collector that collects an iterator of Components and merges them
     */
    static Collector<Component, MutableComponent, Component> joinComponent(Component filler) {
        return Collector.of(
                Component::empty,
                MutableComponent::append,
                (component, component2) -> component.append(filler).append(component2),
                Component::copy
        );
    }

    /**
     * instead of collecting into a map, this collector collects into a {@link MapStream}
     * @see Collectors#toMap(Function, Function)
     */
    static <T, K , L>  Collector<T, HashMap<K, L>, MapStream<K , L>> toMapStream(Function<T, K> keyMapper, Function<T, L> valueMapper) {
        return Collector.of(HashMap::new, (hashMap, t) -> hashMap.put(keyMapper.apply(t), valueMapper.apply(t)), CollectorHelper::mergeMap, MapStream::of);
    }

    /**
     * instead of collecting into a map, this collector collects into a {@link MapStream}
     * @see CollectorHelper#toValueMappedStream(Function)
     */
    static <T, L>  Collector<T, HashMap<T, L>, MapStream<T , L>> toValueMappedStream(Function<T, L> valueMapper) {
        return Collector.of(HashMap::new, (hashMap, t) -> hashMap.put(t, valueMapper.apply(t)), CollectorHelper::mergeMap, MapStream::of);
    }

    /**
     * instead of collecting into a map, this collector collects into a {@link MapStream}
     * @see CollectorHelper#toKeyMappedStream(Function)
     */
    static <T, K> Collector<T, HashMap<K ,T>, MapStream<K, T>> toKeyMappedStream(Function<T, K> keyMapper) {
        return Collector.of(HashMap::new, (hashMap, t) -> hashMap.put(keyMapper.apply(t), t), CollectorHelper::mergeMap, MapStream::of);
    }

    /**
     * merge two maps into another
     */
    private static <T, K> HashMap<T, K> mergeMap(HashMap<T, K> map, HashMap<T, K> map1) {
        map.putAll(map1);
        return map;
    }
}
