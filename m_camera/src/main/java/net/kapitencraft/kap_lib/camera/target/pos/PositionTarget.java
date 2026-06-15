package net.kapitencraft.kap_lib.camera.target.pos;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
    Codec<Builder<?>> CODEC = Types.CODEC.dispatch(Builder::getType, types -> types.type.codec());
    StreamCodec<? super RegistryFriendlyByteBuf, PositionTarget> STREAM_CODEC = StreamCodec.of(Types::toNw, PositionTarget::fromNw);

    static PositionTarget fromNw(RegistryFriendlyByteBuf buf) {
        Types t = Types.values()[buf.readInt()];
        return t.type.streamCodec().decode(buf);
    }

    /**
     * @return a position target for the given position
     */
    static Builder<?> fixed(Vec3 pos) {
        return new StaticPositionTarget.Builder().setPos(pos);
    }

    static Builder<?> relative(Builder<?> pos, Vec3 offset) {
        return new RelativePositionTarget.Builder().setTarget(pos).setOffset(offset);
    }

    /**
     * @return a position target tracking the entity's position
     */
    static Builder<EntityPositionTarget> entity(Entity entity) {
        return new EntityPositionTarget.Builder().setTarget(entity.getUUID());
    }

    static Builder<EntityPositionTarget> entityEyes(Entity entity) {
        return new EntityPositionTarget.Builder().setTarget(entity.getUUID()).eyes();
    }

    static Builder<EntityBBPositionTarget> entityBB(Entity entity) {
        return EntityBBPositionTarget.builder(entity);
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

        MapCodec<? extends Builder<T>> codec();

        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
    }

    interface Builder<T extends PositionTarget> {

        T build();

        Types getType();
    }
}
