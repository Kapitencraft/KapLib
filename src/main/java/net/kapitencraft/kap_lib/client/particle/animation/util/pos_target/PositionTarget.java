package net.kapitencraft.kap_lib.client.particle.animation.util.pos_target;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.function.Supplier;

/**
 * position target interface
 * provides positions for spawning / moving particles
 */
public interface PositionTarget {

    static PositionTarget fromNw(FriendlyByteBuf buf) {
        Types t = Types.values()[buf.readInt()];
        return t.type.fromNw(buf);
    }

    /**
     * @return a position target for the given position
     */
    static PositionTarget fixed(Vec3 pos) {
        return new AbsolutePositionTarget(pos);
    }

    /**
     * @return a position target tracking the entity's position
     */
    static PositionTarget entity(Entity entity) {
        return new EntityPositionTarget(entity.getId());
    }

    /**
     * @return the current position
     */
    Vec3 pos();

    Types getType();

    default void toNw(FriendlyByteBuf buf) {
        Types.toNw(buf, this);
    }

    enum Types implements IExtensibleEnum {
        ENTITY(EntityPositionTarget.Type::new),
        POS(AbsolutePositionTarget.Type::new);

        private final Type<? extends PositionTarget> type;

        Types(Supplier<Type<? extends PositionTarget>> typeSupplier) {
            this.type = typeSupplier.get();
        }

        private static <T extends PositionTarget> void toNw(FriendlyByteBuf buf, T val) {
            Types types = val.getType();
            buf.writeInt(types.ordinal());
            ((Type<T>) types.type).toNw(buf, val);
        }

        public static Types create(String name, Supplier<Type<? extends PositionTarget>> typeSupplier) {
            throw new IllegalAccessError("enum not extended!");
        }
    }

    interface Type<T extends PositionTarget> {
        void toNw(FriendlyByteBuf buf, T val);

        T fromNw(FriendlyByteBuf buf);
    }
}
