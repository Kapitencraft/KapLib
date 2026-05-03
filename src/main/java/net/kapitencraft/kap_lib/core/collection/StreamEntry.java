package net.kapitencraft.kap_lib.core.collection;

/**
 * used for {@link MapStream}
 * @param <K> map key type
 * @param <V> map value type
 * @param k map key
 * @param v map value
 */
public record StreamEntry<K, V>(K k, V v) {
}
