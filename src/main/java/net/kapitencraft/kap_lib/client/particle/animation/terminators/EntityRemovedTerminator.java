package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.CaretListener;
import java.util.Objects;

public class EntityRemovedTerminator implements AnimationTerminator {
    private final int target;

    public EntityRemovedTerminator(int target) {
        this.target = target;
    }

    public static Builder builder(Entity target) {
        return new Builder(target.getId());
    }

    @Override
    public @NotNull Type getType() {
        return TerminatorTypes.ENTITY_REMOVED.get();
    }

    @Override
    public boolean shouldTerminate(ParticleAnimator animation) {
        Entity entity = ClientHelper.getNullableEntity(target);
        return entity == null || entity.isRemoved();
    }

    public static class Type implements AnimationTerminator.Type<EntityRemovedTerminator> {

        @Override
        public void toNw(FriendlyByteBuf buf, EntityRemovedTerminator val) {
            buf.writeInt(val.target);
        }

        @Override
        public EntityRemovedTerminator fromNw(FriendlyByteBuf buf) {
            return new EntityRemovedTerminator(buf.readInt());
        }
    }

    public static class Builder implements AnimationTerminator.Builder {
        private final int entity;

        public Builder(int entity) {
            this.entity = entity;
        }

        @Override
        public AnimationTerminator build() {
            return new EntityRemovedTerminator(entity);
        }
    }

    @Override
    public String toString() {
        return "EntityRemovedTerminator[" + target + "]";
    }
}
