package net.kapitencraft.kap_lib.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.item.modifier_display.ItemModifiersDisplayExtension;
import net.kapitencraft.kap_lib.stream.Consumers;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface BiCollectors {

    static <K, V> BiCollector<K, V, HashMap<K, V>, HashMap<K, V>> toMap() {
        return new BiCollectorImpl<>(HashMap::new, HashMap::put, Function.identity());
    }

    static BiCollector<String, JsonElement, JsonObject, JsonObject> mergeJson() {
        return new BiCollectorImpl<>(JsonObject::new, JsonObject::add, Function.identity());
    }

    static <J, V, K> BiCollector<K, V, List<J>, Stream<J>> toStream(BiFunction<K, V, J> mapper) {
        return new BiCollectorImpl<>(ArrayList::new, (js, k, v) -> js.add(mapper.apply(k, v)), List::stream);
    }

    static <V, K> BiCollector<K, V, List<Pair<K, V>>, List<Pair<K, V>>> toPairList() {
        return new BiCollectorImpl<>(ArrayList::new, (pairs, k, v) -> pairs.add(new Pair<>(k, v)), ImmutableList::copyOf);
    }


    record BiCollectorImpl<K, V, A, R>(Supplier<A> supplier,
                                       Consumers.C3<A, K, V> accumulator,
                                       Function<A, R> finisher
    ) implements BiCollector<K, V, A, R> {
    }
}
