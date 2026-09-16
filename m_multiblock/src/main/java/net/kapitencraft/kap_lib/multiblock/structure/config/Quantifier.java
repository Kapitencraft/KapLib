package net.kapitencraft.kap_lib.multiblock.structure.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

public abstract class Quantifier {
    public static final Codec<Quantifier> CODEC = Type.CODEC.dispatch(Quantifier::getType, Type::getCodec);

    public static Quantifier range(int min, int max) {
        return new RangeQuantifier(min, max);
    }

    public static Quantifier atLeast(int min) {
        return new AtLeastQuantifier(min);
    }

    public abstract boolean allows(int count);

    protected abstract Type getType();

    protected enum Type implements StringRepresentable {
        AT_MOST_ONCE(AtMostOnceQuantifier.CODEC),
        ANY(AnyQuantifier.CODEC),
        AT_LEAST_ONCE(AtLeastOnceQuantifier.CODEC),
        RANGE(RangeQuantifier.CODEC),
        AT_LEAST(AtLeastQuantifier.CODEC),
        AT_MOST(AtMostQuantifier.CODEC);
        private static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final MapCodec<? extends Quantifier> obj;

        Type(MapCodec<? extends Quantifier> obj) {
            this.obj = obj;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        public MapCodec<? extends Quantifier> getCodec() {
            return obj;
        }
    }

    private static class AtMostOnceQuantifier extends Quantifier {
        private static final MapCodec<AtLeastOnceQuantifier> CODEC = MapCodec.unit(new AtLeastOnceQuantifier());

        @Override
        public boolean allows(int count) {
            return count <= 1;
        }

        @Override
        protected Type getType() {
            return Type.AT_MOST_ONCE;
        }
    }

    private static class AnyQuantifier extends Quantifier {
        private static final MapCodec<AnyQuantifier> CODEC = MapCodec.unit(new AnyQuantifier());

        @Override
        public boolean allows(int count) {
            return true;
        }

        @Override
        protected Type getType() {
            return Type.ANY;
        }
    }

    private static class AtLeastOnceQuantifier extends Quantifier {
        private static final MapCodec<AtLeastOnceQuantifier> CODEC = MapCodec.unit(new AtLeastOnceQuantifier());

        @Override
        public boolean allows(int count) {
            return count >= 1;
        }

        @Override
        protected Type getType() {
            return Type.AT_LEAST_ONCE;
        }
    }

    private static class RangeQuantifier extends Quantifier {
        private static final MapCodec<RangeQuantifier> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.fieldOf("min").forGetter(r -> r.min),
                Codec.INT.fieldOf("max").forGetter(r -> r.max)
        ).apply(i, RangeQuantifier::new));
        private final int min, max;

        private RangeQuantifier(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean allows(int count) {
            return count >= min && count <= max;
        }

        @Override
        protected Type getType() {
            return Type.RANGE;
        }
    }

    private static class AtLeastQuantifier extends Quantifier {
        private static final MapCodec<AtLeastQuantifier> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.fieldOf("min").forGetter(a -> a.min)
        ).apply(i, AtLeastQuantifier::new));
        private final int min;

        private AtLeastQuantifier(int min) {
            this.min = min;
        }

        @Override
        public boolean allows(int count) {
            return count >= min;
        }

        @Override
        protected Type getType() {
            return Type.AT_LEAST;
        }
    }

    private static class AtMostQuantifier extends Quantifier {
        private static final MapCodec<AtMostQuantifier> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.INT.fieldOf("max").forGetter(a -> a.max)
        ).apply(i, AtMostQuantifier::new));
        private final int max;

        private AtMostQuantifier(int max) {
            this.max = max;
        }

        @Override
        public boolean allows(int count) {
            return count <= max;
        }

        @Override
        protected Type getType() {
            return null;
        }
    }
}