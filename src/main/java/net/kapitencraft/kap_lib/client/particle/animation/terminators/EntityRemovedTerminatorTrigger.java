package net.kapitencraft.kap_lib.client.particle.animation.terminators;

import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.SimpleTerminationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EntityRemovedTerminatorTrigger extends SimpleTerminationTrigger<EntityRemovedTerminatorTrigger.Instance> {
    private static final StreamCodec<? super RegistryFriendlyByteBuf, Instance> STREAM_CODEC = ByteBufCodecs.INT.map(Instance::new, Instance::entityId),

    public static TerminationTriggerInstance create(Entity target) {
        return new Instance(target.getId());
    }

    public void trigger(int entityId) {
        this.trigger(instance -> instance.entityId == entityId);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, Instance> codec() {
        return STREAM_CODEC;
    }

    public record Instance(int entityId) implements TerminationTriggerInstance {

        @Override
        public @NotNull EntityRemovedTerminatorTrigger getTrigger() {
            return TerminatorTriggers.ENTITY_REMOVED.get();
        }
    }
}
