package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MoveTowardsBBElement implements AnimationElement {
    private final int entity, duration;

    public MoveTowardsBBElement(int entity, int duration) {
        this.entity = entity;
        this.duration = duration;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public @NotNull Type getType() {
        return ElementTypes.MOVE_TOWARDS_BB.get();
    }


    @Override
    public int createLength(ParticleConfig config) {
        return duration;
    }

    @Override
    public void initialize(ParticleConfig object) {
        object.setProperty("target", MathHelper.randomIn(KapLibMod.RANDOM_SOURCE, ClientHelper.getEntity(entity).getBoundingBox()).subtract(ClientHelper.getEntity(entity).position()));
        object.setProperty("origin", object.pos());
    }

    @Override
    public void tick(ParticleConfig object, int tick) {
        object.setPos(object.<Vec3>getProperty("origin").lerp(object.<Vec3>getProperty("target").add(ClientHelper.getEntity(entity).position()), (tick + 1f) / duration));
    }

    public static class Builder implements AnimationElement.Builder {
        private Entity entity;
        private int duration;

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder target(Entity entity) {
            this.entity = entity;
            return this;
        }

        @Override
        public AnimationElement build() {
            if (duration < 1) throw new IllegalStateException("MoveTowardsBB duration must be larger than 0");
            return new MoveTowardsBBElement(Objects.requireNonNull(entity, "MoveTowardsBB without entity found!").getId(), duration);
        }
    }

    public static class Type implements AnimationElement.Type<MoveTowardsBBElement> {

        @Override
        public MoveTowardsBBElement fromNW(FriendlyByteBuf buf) {
            return new MoveTowardsBBElement(buf.readInt(), buf.readInt());
        }

        @Override
        public void toNW(FriendlyByteBuf buf, MoveTowardsBBElement value) {
            buf.writeInt(value.entity);
            buf.writeInt(value.duration);
        }
    }
}
