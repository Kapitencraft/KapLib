package net.kapitencraft.kap_lib.client.particle.animation.modifiers;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleConfig;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MoveTowardsElement implements AnimationElement {
    private final Vec3 targetLoc;

    public MoveTowardsElement(Vec3 targetLoc) {
        this.targetLoc = targetLoc;
    }

    public static Builder builder() {
        return new Builder();
    }


    @Override
    public @NotNull AnimationElement.Type<? extends AnimationElement> getType() {
        return null;
    }

    @Override
    public int createLength(ParticleConfig config) {
        return 0;
    }

    @Override
    public void tick(ParticleConfig object, int tick) {


    }

    public static class Builder implements AnimationElement.Builder {
        private Vec3 targetLoc;

        public Builder target(Vec3 pos) {
            targetLoc = pos;
            return this;
        }


        @Override
        public AnimationElement build() {
            return new MoveTowardsElement(targetLoc);
        }
    }

    public static class Type implements AnimationElement.Type<MoveTowardsElement> {

        @Override
        public MoveTowardsElement fromNW(FriendlyByteBuf buf, ClientLevel level) {
            return new MoveTowardsElement(NetworkHelper.readVec3(buf));
        }

        @Override
        public void toNW(FriendlyByteBuf buf, MoveTowardsElement value) {
            NetworkHelper.writeVec3(buf, value.targetLoc);
        }
    }
}
