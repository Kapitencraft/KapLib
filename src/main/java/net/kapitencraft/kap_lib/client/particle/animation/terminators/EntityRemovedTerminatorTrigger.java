package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.SimpleTerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityRemovedTerminatorTrigger extends SimpleTerminationTrigger<EntityRemovedTerminatorTrigger.Instance> {

    public static TerminationTriggerInstance create(Entity target) {
        return new Instance(target.getId());
    }

    @Override
    public void toNw(FriendlyByteBuf buf, Instance terminator) {
        buf.writeInt(terminator.entityId);
    }

    public void trigger(int entityId) {
        this.trigger(instance -> instance.entityId == entityId);
    }

    @Override
    public Instance fromNw(FriendlyByteBuf buf) {
        return new Instance(buf.readInt());
    }

    public record Instance(int entityId) implements TerminationTriggerInstance {

        @Override
        public @NotNull EntityRemovedTerminatorTrigger getTrigger() {
            return TerminatorTriggers.ENTITY_REMOVED.get();
        }
    }
}
