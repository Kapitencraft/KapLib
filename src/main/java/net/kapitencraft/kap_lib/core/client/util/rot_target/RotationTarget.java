package net.kapitencraft.kap_lib.core.client.util.rot_target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.store.EntityAccessor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * rotation target interface
 * provides rotations for spawning / moving particles
 */
public interface RotationTarget extends Supplier<Vec2> {
    Codec<RotationTarget.Builder<?>> CODEC = Types.CODEC.dispatch(RotationTarget.Builder::type, types -> types.type.codec());
    StreamCodec<RegistryFriendlyByteBuf, RotationTarget> STREAM_CODEC = StreamCodec.of(Types::toNw, RotationTarget::fromNw);

    static RotationTarget fromNw(RegistryFriendlyByteBuf buf) {
        Types t = Types.values()[buf.readInt()];
        return t.type.streamCodec().decode(buf);
    }

    static RotationTarget.Builder<?> absolute(float x, float y) {
        return new StaticRotationTarget.Builder().setRot(new Vec2(x, y));
    }

    static RotationTarget.Builder<?> absolute(Vec2 rot) {
        return new StaticRotationTarget.Builder().setRot(rot);
    }

    static RotationTarget.Builder<?> forEntity(Entity entity) {
        return new FromEntityRotationTarget.Builder().setAccessor(EntityAccessor.direct(entity));
    }

    Vec2 get();

    Types getType();

    enum Types implements StringRepresentable, IExtensibleEnum {
        TRACK_POSITION(TrackPositionRotationTarget.Type::new),
        ABSOLUTE(StaticRotationTarget.Type::new),
        FROM_ENTITY(FromEntityRotationTarget.Type::new);

        private static final Codec<Types> CODEC = StringRepresentable.fromEnum(Types::values);

        private final Type<? extends RotationTarget> type;

        Types(Supplier<Type<? extends RotationTarget>> typeSupplier) {
            this.type = typeSupplier.get();
        }

        private static <T extends RotationTarget> void toNw(RegistryFriendlyByteBuf buf, T val) {
            Types types = val.getType();
            buf.writeInt(types.ordinal());
            ((Type<T>) types.type).streamCodec().encode(buf, val);
        }

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase();
        }
    }
    interface Type<T extends RotationTarget> {
        MapCodec<? extends Builder<T>> codec();
        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
    }

    interface Builder<T extends RotationTarget> {

        T build(Map<String, UUID> context);

        Types type();
    }
}
