package net.kapitencraft.kap_lib.client.particle.animation.elements;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.client.particle.animation.util.pos_target.PositionTarget;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ElementTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MoveTowardsElement implements AnimationElement {
    private final PositionTarget targetLoc;
    private final int duration;

    public MoveTowardsElement(PositionTarget targetLoc, int duration) {
        this.targetLoc = targetLoc;
        this.duration = duration;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void initialize(ParticleConfig object) {
        object.setProperty("origin", object.pos());
    }

    @Override
    public @NotNull AnimationElement.Type<? extends AnimationElement> getType() {
        return ElementTypes.MOVE_TOWARDS.get();
    }

    @Override
    public int createLength(ParticleConfig config) {
        return duration;
    }

    @Override
    public void tick(ParticleConfig object, int tick) {
        object.setPos(object.<Vec3>getProperty("origin").lerp(targetLoc.get(), tick / (duration - 1f)));
    }

    public static class Builder implements AnimationElement.Builder {
        private PositionTarget targetLoc;
        private int duration;

        public Builder target(PositionTarget pos) {
            targetLoc = pos;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }


        @Override
        public AnimationElement build() {
            return new MoveTowardsElement(targetLoc, duration);
        }
    }

    public static class Type implements AnimationElement.Type<MoveTowardsElement> {

        @Override
        public MoveTowardsElement fromNW(FriendlyByteBuf buf) {
            return new MoveTowardsElement(PositionTarget.fromNw(buf), buf.readInt());
        }

        @Override
        public void toNW(FriendlyByteBuf buf, MoveTowardsElement value) {
            value.targetLoc.toNw(buf);
            buf.writeInt(value.duration);
        }
    }
}
