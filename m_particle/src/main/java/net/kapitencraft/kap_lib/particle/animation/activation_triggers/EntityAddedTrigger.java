package net.kapitencraft.kap_lib.particle.animation.activation_triggers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTriggerInstance;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ActivationTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class EntityAddedTrigger implements ActivationTrigger<EntityAddedTrigger.InstanceActivation> {
    private static final MapCodec<InstanceActivation> CODEC = Codec.INT.xmap(InstanceActivation::new, i -> i.entityId).fieldOf("entity");
    private static final StreamCodec<? super RegistryFriendlyByteBuf, InstanceActivation> STREAM_CODEC = ByteBufCodecs.INT.map(InstanceActivation::new, i -> i.entityId);

    private final Multimap<Integer, Listener<InstanceActivation>> instances = HashMultimap.create();

    public static ActivationTriggerInstance forEntity(Entity entity) {
        return new InstanceActivation(entity.getId());
    }

    @Override
    public void addListener(Listener<InstanceActivation> instance) {
        instances.put(instance.getTrigger().entityId, instance);
    }

    @Override
    public void removeListener(Listener<InstanceActivation> instance) {
        instances.remove(instance.getTrigger().entityId, instance);
    }

    @Override
    public boolean active(Listener<InstanceActivation> instance) {
        return Objects.requireNonNull(Minecraft.getInstance().level).getEntity(instance.getTrigger().entityId) != null;
    }

    @Override
    public MapCodec<InstanceActivation> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, InstanceActivation> streamCodec() {
        return STREAM_CODEC;
    }

    public void trigger(int id) {
        instances.get(id).forEach(Listener::run);
        instances.removeAll(id);
    }

    public static class InstanceActivation implements ActivationTriggerInstance {
        private final int entityId;

        public InstanceActivation(int entityId) {
            this.entityId = entityId;
        }

        @Override
        public EntityAddedTrigger getTrigger() {
            return ActivationTriggers.ENTITY_ADDED.get();
        }

        @Override
        public String toString() {
            return "EntityAddedTrigger[" + entityId + ']';
        }
    }
}
