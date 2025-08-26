package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.SimpleTerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BonusRemovedTerminator extends SimpleTerminationTrigger<BonusRemovedTerminator.Instance> {

    public static Instance create(Entity entity, ResourceLocation elementId) {
        return new Instance(entity.getId(), elementId);
    }

    public void trigger(int entityId, ResourceLocation elementId) {
        this.trigger(i -> i.entityId == entityId && i.elementId == elementId);
    }

    @Override
    public void toNw(FriendlyByteBuf buf, Instance terminator) {
        buf.writeInt(terminator.entityId);
        buf.writeResourceLocation(terminator.elementId);
    }

    @Override
    public Instance fromNw(FriendlyByteBuf buf) {
        return new Instance(buf.readInt(), buf.readResourceLocation());
    }

    public static class Instance implements TerminationTriggerInstance {
        private final int entityId;
        private final ResourceLocation elementId;

        public Instance(int entityId, ResourceLocation elementId) {
            this.entityId = entityId;
            this.elementId = elementId;
        }

        @Override
        public TerminationTrigger<Instance> getTrigger() {
            return TerminatorTriggers.BONUS_REMOVED.get();
        }
    }
}
