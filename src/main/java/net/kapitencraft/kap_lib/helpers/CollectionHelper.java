package net.kapitencraft.kap_lib.helpers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CollectionHelper {

    /**
     * @return the first key in the given map
     */
    static <T, K> @Nullable T getFirstKey(Map<T, K> collection) {
        return collection.keySet().stream().findFirst().orElse(null);
    }

    /**
     * @return the key for the given value
     */
    static <T, K> T getKeyForValue(Map<T, K> map, K value) {
        for (Map.Entry<T, K> entry : map.entrySet()) {
            if (entry.getValue() == value) return entry.getKey();
        }
        return null;
    }

    /**
     * swaps Keys with values
     * @return the mirrored map
     */
    static <T, K> Map<K, T> mirror(Map<T, K> in) {
        Map<K, T> returnMap = new HashMap<>();
        in.forEach((t, k) -> returnMap.put(k, t));
        return returnMap;
    }

    /**
     * sorts the given Multimap by the given sorter arguments
     * @return the sorted multimap
     */
    static <T, K> Multimap<T, K> sortMap(Multimap<T, K> map, @Nullable Comparator<T> keySorter, @Nullable Comparator<K> valueSorter) {
        List<T> sortedKeys = fromAny(map.keySet());
        MiscHelper.ifNonNull(keySorter, sortedKeys::sort);
        Multimap<T, K> multimap = HashMultimap.create();
        sortedKeys.forEach(t -> {
            List<K> values = mutableList(fromAny(map.get(t)));
            MiscHelper.ifNonNull(valueSorter, values::sort);
            multimap.putAll(t, values);
        });
        return multimap;
    }

    /**
     * @param src the array to search
     * @param obj the object to find
     * @return the index of the object in the array, or {@code -1} if it wasn't contained
     */
    static <T> int index(T[] src, T obj) {
        for (int i = 0; i < src.length; i++) {
            if (src[i].equals(obj)) return i;
        }
        return -1;
    }

    /**
     * @return a new, modifiable list
     */
    static <T> List<T> mutableList(Collection<T> immutable) {
        return new ArrayList<>(immutable);
    }

    /**
     * creates a List from any collection
     */
    static <T> List<T> fromAny(Collection<T> collection) {
        return collection.stream().toList();
    }

    /**
     * removes any values that match the given predicate using the collection's next value as the second argument in the predicate
     */
    static <T> void removeMapping2(List<T> ts, BiPredicate<T, T> predicate) {
        ts.removeIf(t -> {
            int id = ts.indexOf(t);
            T second;
            if (id < ts.size() - 1) {
                second = ts.get(id + 1);
            } else {
                second = ts.get(0);
            }
            return predicate.test(t, second);
        });
    }

    /**
     * similar to removeMapping2 but instead of removing matching it's consumed
     * @see CollectionHelper#removeMapping2(List, BiPredicate)
     */
    static <T> void forEachMapping2(List<T> ts, BiConsumer<T, T> consumer) {
        ts.forEach(t -> {
            int id = ts.indexOf(t);
            T second;
            if (id < ts.size() - 1) {
                second = ts.get(id + 1);
            } else {
                second = ts.get(0);
            }
            consumer.accept(t, second);
        });
    }

    /**
     * @param size the size the list should be grown to
     * @param sup the
     * @param <T>
     * @return
     */
    static <T> List<T> create(int size, Supplier<T> sup) {
        List<T> list = new ArrayList<>();
        MiscHelper.repeat(size, integer -> list.add(sup.get()));
        return list;
    }

    /**
     * create a new Multimap from a single element Map
     */
    static <T, K> Multimap<T, K> fromMap(Map<T, K> map) {
        Multimap<T, K> multimap = HashMultimap.create();
        for (T t : map.keySet()) {
            multimap.put(t, map.get(t));
        }
        return multimap;
    }

    /**
     * create a new modifiable map from a single element
     */
    static <T> ArrayList<T> toList(T ts) {
        ArrayList<T> target = new ArrayList<>();
        Collections.addAll(target, ts);
        return target;
    }


    /**
     * get all elements of a Map containing a Map
     * @see net.kapitencraft.kap_lib.collection.DoubleMap DoubleMap
     */
    static <T, K, L, J extends Map<K, L>> List<L> values(Map<T, J> map) {
        return map.values().stream().map(Map::values).flatMap(Collection::stream).toList();
    }

    /**
     * @return the first value of the map, or null if the map is empty
     */
    static <T, K> K getFirstValue(Map<T, K> map) {
        for (Map.Entry<T, K> entry : map.entrySet()) {
            return entry.getValue();
        }
        return null;
    }


    /**
     * remove any entry which key matches the predicate
     */
    static <K, V> void removeIf(Map<K, V> map, Predicate<K> predicate) {
        for (K k : map.keySet()) {
            if (predicate.test(k)) {
                map.remove(k);
            }
        }
    }

    /**
     * do something with each value inside this map of maps
     */
    static <T, V> void forEach(Map<T, Map<T, V>> map, BiConsumer<? super T, ? super V> consumer) {
        for (Map<T, V> map1 : map.values()) {
            map1.forEach(consumer);
        }
    }

    /**
     * @return the list in inverse order
     */
    static  <V> ArrayList<V> invertList(ArrayList<V> list) {
        ArrayList<V> out = new ArrayList<>();
        for (int i = list.size(); i > 0; i--) {

            out.add(list.get((i - 1)));
        }
        return out;
    }

    /**
     * sorts the given Living entities by their distance to the given source
     */
    @Contract("null, _ -> fail; _, null -> fail")
    static List<LivingEntity> sortLowestDistance(Entity source, List<LivingEntity> list) {
        if (list.isEmpty()) {
            return List.of();
        }
        return list.stream().sorted(Comparator.comparingDouble(living -> living.distanceToSqr(source.position()))).collect(Collectors.toList());
    }

    /**
     * checks if the given array contains the given value
     */
    static <T> boolean arrayContains(T[] array, T t) {
        return List.of(array).contains(t);
    }


    /**
     * merge the stream into the given {@code T} var type
     */
    static <S, T extends S> Stream<T> cast(Stream<S> in, Class<T> clazz) {
        return in.filter(clazz::isInstance).map(clazz::cast);
    }


    /**
     * @param split the array
     * @return the element at the highest index
     */
    static String getLast(@NotNull String[] split) {
        if (split.length == 0) throw new IndexOutOfBoundsException("can not get last index from empty array");
        return split[split.length-1];
    }
}
