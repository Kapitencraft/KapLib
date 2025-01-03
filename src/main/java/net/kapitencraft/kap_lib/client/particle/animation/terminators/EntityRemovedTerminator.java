package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.ParticleAnimation;
import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityRemovedTerminator implements AnimationTerminator {
    private final Entity target;

    public EntityRemovedTerminator(Entity target) {
        this.target = target;
    }

    public static Builder builder(Entity target) {
        return new Builder(target);
    }

    @Override
    public @NotNull Type getType() {
        return TerminatorTypes.ENTITY_REMOVED.get();
    }

    @Override
    public boolean shouldTerminate(ParticleAnimator animation) {
        return target.isRemoved();
    }

    public static class Type implements AnimationTerminator.Type<EntityRemovedTerminator> {

        @Override
        public void toNw(FriendlyByteBuf buf, EntityRemovedTerminator val) {
            buf.writeInt(val.target.getId());
        }

        @Override
        public EntityRemovedTerminator fromNw(FriendlyByteBuf buf) {
            Entity target = Objects.requireNonNull(Minecraft.getInstance().level).getEntity(buf.readInt());
            return new EntityRemovedTerminator(target);
        }
    }

    public static class Builder implements AnimationTerminator.Builder {
        private final Entity entity;

        public Builder(Entity entity) {
            this.entity = entity;
        }

        @Override
        public AnimationTerminator build() {
            return new EntityRemovedTerminator(entity);
        }
    }
}
