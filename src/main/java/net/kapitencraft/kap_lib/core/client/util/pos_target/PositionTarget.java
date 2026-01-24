package net.kapitencraft.kap_lib.core.client.util.pos_target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

import java.util.function.Supplier;

/**
 * position target interface
 * provides positions for spawning / moving particles
 */
public interface PositionTarget extends Supplier<Vec3> {
    Codec<PositionTarget> CODEC = Types.CODEC.dispatch(PositionTarget::getType, types -> types.type.codec());
    StreamCodec<? super RegistryFriendlyByteBuf, PositionTarget> STREAM_CODEC = StreamCodec.of(Types::toNw, PositionTarget::fromNw);

    static PositionTarget fromNw(RegistryFriendlyByteBuf buf) {
        Types t = Types.values()[buf.readInt()];
        return t.type.streamCodec().decode(buf);
    }

    /**
     * @return a position target for the given position
     */
    static PositionTarget fixed(Vec3 pos) {
        return new StaticPositionTarget(pos);
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
        return new EntityBBPositionTarget(entity.getId());
    }

    /**
     * @return the current position
     */
    Vec3 get();

    Types getType();

    enum Types implements StringRepresentable, IExtensibleEnum {
        ENTITY(EntityPositionTarget.Type::new),
        ENTITY_BB(EntityBBPositionTarget.Type::new),
        POS(StaticPositionTarget.Type::new),
        RELATIVE(RelativePositionTarget.Type::new);

        private static final Codec<Types> CODEC = StringRepresentable.fromEnum(Types::values);

        private final Type<? extends PositionTarget> type;

        Types(Supplier<Type<? extends PositionTarget>> typeSupplier) {
            this.type = typeSupplier.get();
        }

        private static <T extends PositionTarget> void toNw(RegistryFriendlyByteBuf buf, T val) {
            Types types = val.getType();
            buf.writeInt(types.ordinal());
            ((Type<T>) types.type).streamCodec().encode(buf, val);
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    interface Type<T extends PositionTarget> {

        MapCodec<T> codec();
        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
    }
}
