package net.kapitencraft.kap_lib.core.io.serialization;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.function.Supplier;

/**
 * a simple serializer saving data in NBT
 * @param <T> type to serialize
 */
public class NbtSerializer<T> extends Serializer<Tag, NbtOps, T> {
    public NbtSerializer(Codec<T> codec, Supplier<T> defaulted) {
        super(NbtOps.INSTANCE, codec, defaulted);
    }

    public NbtSerializer(Codec<T> codec) {
        this(codec, ()-> null);
    }

    @Override
    Tag getSerializeDefault() {
        return new CompoundTag();
    }
}
