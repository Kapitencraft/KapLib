package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class Serializer<T, K extends DynamicOps<T>, L> {

    private final K generator;
    private final Codec<L> codec;
    private final Supplier<L> defaulted;
    public Serializer(K generator, Codec<L> codec, Supplier<L> defaulted) {
        this.generator = generator;
        this.codec = codec;
        this.defaulted = defaulted;
    }

    abstract T getSerializeDefault();

    public T encode(@NotNull L value) {
        return IOHelper.orElse(codec.encodeStart(generator, value), this::getSerializeDefault);
    }

    /**
     * @deprecated use {@link #parseOrThrow} instead
     */
    @Deprecated()
    public L parse(T object) {
        if (object == null) return defaulted.get();
        return IOHelper.orElse(codec.parse(generator, object), MiscHelper.nonNullOr(defaulted, ()-> null));
    }

    public L parseOrThrow(T object) {
        DataResult<L> result = codec.parse(generator, object);
        return result.resultOrPartial().orElseThrow(NullPointerException::new);
    }

    public Codec<L> getCodec() {
        return codec;
    }
}
