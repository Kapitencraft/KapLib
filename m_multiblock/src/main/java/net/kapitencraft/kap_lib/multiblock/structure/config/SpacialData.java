package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;

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
                Vec3i.CODEC.fieldOf("size").forGetter(sD -> sD.size),
                entryCodec.listOf().fieldOf("entries").forGetter(sD -> sD.lookup),
                SPACIAL_DATA_CODEC.fieldOf("data").forGetter(sD -> sD.data)
        ).apply(i, (v, l, d) -> new SpacialData<>(v, d, l, fallback)));
    }

    private final Vec3i size;
    private final int[][][] data;
    private final List<T> lookup;
    private final T fallback;

    public SpacialData(Vec3i size, int[][][] data, T fallback) {
        this.size = size;
        this.data = data;
        this.lookup = new ArrayList<>();
        this.fallback = fallback;
    }

    private SpacialData(Vec3i size, int[][][] data, List<T> lookup, T fallback) {
        this(size, data, fallback);
        this.lookup.addAll(lookup);
    }

    public T get(int x, int y, int z) {
        int i = data[x][y][z];
        if (i == 0)
            return fallback;
        return lookup.get(i - 1);
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
            data[x][y][z] = idx + 1;
        }
    }

    public Vec3i getSize() {
        return size;
    }
}
