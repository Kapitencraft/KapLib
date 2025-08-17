package net.kapitencraft.kap_lib.client.util.pos_target;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.util.function.Supplier;

/**
 * position target interface
 * provides positions for spawning / moving particles
 */
public interface PositionTarget extends Supplier<Vec3> {
    StreamCodec<FriendlyByteBuf, PositionTarget> STREAM_CODEC = StreamCodec.of(PositionTarget.Types::toNw, PositionTarget::fromNw);

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

    static PositionTarget relative(PositionTarget pos, Vec3 offset) {
        return new RelativePositionTarget(pos, offset);
    }

    /**
     * @return a position target tracking the entity's position
     */
    static PositionTarget entity(Entity entity) {
        return new EntityPositionTarget(entity.getId(), EntityAnchorArgument.Anchor.FEET);
    }

    static PositionTarget entityEyes(Entity entity) {
        return new EntityPositionTarget(entity.getId(), EntityAnchorArgument.Anchor.EYES);
    }

    static PositionTarget entityBB(Entity entity) {
        return new EntityBBPositionTarget(entity);
    }

    /**
     * @return the current position
     */
    Vec3 get();

    Types getType();

    enum Types implements IExtensibleEnum {
        ENTITY(EntityPositionTarget.Type::new),
        ENTITY_BB(EntityBBPositionTarget.Type::new),
        POS(AbsolutePositionTarget.Type::new),
        RELATIVE(RelativePositionTarget.Type::new);

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
