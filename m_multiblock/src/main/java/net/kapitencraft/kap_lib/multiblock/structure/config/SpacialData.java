package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class SpacialData<T> {
    private static final Codec<int[][][]> SPACIAL_DATA_CODEC = Codec.INT_STREAM.xmap(IntStream::toArray, Arrays::stream)
            .listOf().xmap(l -> l.toArray(int[][]::new), Arrays::asList)
            .listOf().xmap(l -> l.toArray(int[][][]::new), Arrays::asList);

    public static <T> Codec<SpacialData<T>> codec(Codec<T> entryCodec, T fallback) {
        return RecordCodecBuilder.create(i -> i.group(
                entryCodec.listOf().fieldOf("entries").forGetter(sD -> sD.lookup),
                SPACIAL_DATA_CODEC.fieldOf("data").forGetter(sD -> sD.data)
        ).apply(i, (l, d) -> new SpacialData<>(d, l, fallback)));
    }

    private final int[][][] data;
    private final List<T> lookup;
    private final T fallback;

    public SpacialData(int[][][] data, T fallback) {
        this.data = data;
        this.lookup = new ArrayList<>();
        this.fallback = fallback;
    }

    private SpacialData(int[][][] data, List<T> lookup, T fallback) {
        this(data, fallback);
        this.lookup.addAll(lookup);
    }

    public T get(int x, int y, int z) {
        return lookup.get(data[x][y][z]);
    }

    public void set(int x, int y, int z, T value) {
        if (value != fallback) {
            int idx;
            if (lookup.contains(value)) {
                idx = lookup.indexOf(value);
            } else {
                idx = lookup.size();
                lookup.add(value);
            }
            data[x][y][z] = idx;
        }
    }
}
