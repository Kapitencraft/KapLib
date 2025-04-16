package net.kapitencraft.kap_lib.collection;

import net.kapitencraft.kap_lib.stream.Consumers;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * collector for MapStreams.<br>
 * does not (yet) allow for parallel processing
 * @see java.util.stream.Collector Collector
 */
public interface BiCollector<K, V, A, R> {

    Supplier<A> supplier();

    Consumers.C3<A, K, V> accumulator();

    Function<A, R> finisher();
}
