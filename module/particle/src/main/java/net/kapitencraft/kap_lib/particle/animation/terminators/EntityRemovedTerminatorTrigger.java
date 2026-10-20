package net.kapitencraft.kap_lib.particle.animation.terminators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.SimpleTerminationTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.core.TerminationTriggerInstance;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityRemovedTerminatorTrigger extends SimpleTerminationTrigger<EntityRemovedTerminatorTrigger.Instance> {
    private static final MapCodec<Instance> CODEC = Codec.INT.xmap(Instance::new, Instance::entityId).fieldOf("target");
    private static final StreamCodec<? super RegistryFriendlyByteBuf, Instance> STREAM_CODEC = ByteBufCodecs.INT.map(Instance::new, Instance::entityId);

    public static TerminationTriggerInstance create(Entity target) {
        return new Instance(target.getId());
    }

    public void trigger(int entityId) {
        this.trigger(instance -> instance.entityId == entityId);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, Instance> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public MapCodec<Instance> codec() {
        return CODEC;
    }

    public record Instance(int entityId) implements TerminationTriggerInstance {

        @Override
        public @NotNull EntityRemovedTerminatorTrigger getTrigger() {
            return TerminatorTriggers.ENTITY_REMOVED.get();
        }
    }
}
