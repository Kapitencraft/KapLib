package net.kapitencraft.kap_lib.client.util.rot_target;

import net.kapitencraft.kap_lib.client.util.pos_target.PositionTarget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.util.function.Supplier;

/**
 * rotation target interface
 * provides rotations for spawning / moving particles
 */
public interface RotationTarget extends Supplier<Vec2> {
    public static final StreamCodec<FriendlyByteBuf, RotationTarget> CODEC = StreamCodec.of(Types::toNw, RotationTarget::fromNw);

    static RotationTarget fromNw(FriendlyByteBuf buf) {
        Types t = Types.values()[buf.readInt()];
        return t.type.fromNw(buf);
    }

    static RotationTarget absolute(float x, float y) {
        return new AbsoluteRotationTarget(new Vec2(x, y));
    }

    static RotationTarget absolute(Vec2 rot) {
        return new AbsoluteRotationTarget(rot);
    }

    static RotationTarget forEntity(Entity entity) {
        return new FromEntityRotationTarget(entity.getId());
    }

    Vec2 get();

    RotationTarget.Types getType();

    enum Types implements IExtensibleEnum {
        TRACK_POSITION(TrackPositionRotationTarget.Type::new),
        ABSOLUTE(AbsoluteRotationTarget.Type::new),
        FROM_ENTITY(FromEntityRotationTarget.Type::new);

        private final RotationTarget.Type<? extends RotationTarget> type;

        Types(Supplier<RotationTarget.Type<? extends RotationTarget>> typeSupplier) {
            this.type = typeSupplier.get();
        }

        private static <T extends RotationTarget> void toNw(FriendlyByteBuf buf, T val) {
            RotationTarget.Types types = val.getType();
            buf.writeInt(types.ordinal());
            ((RotationTarget.Type<T>) types.type).toNw(buf, val);
        }

        public static RotationTarget.Types create(String name, Supplier<PositionTarget.Type<? extends PositionTarget>> typeSupplier) {
            throw new IllegalAccessError("enum not extended!");
        }
    }
    interface Type<T extends RotationTarget> {
        void toNw(FriendlyByteBuf buf, T val);

        T fromNw(FriendlyByteBuf buf);
    }
}
