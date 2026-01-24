package net.kapitencraft.kap_lib.particle.animation.activation_triggers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.particle.animation.activation_triggers.core.TriggerInstance;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ActivationTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class EntityAddedTrigger implements ActivationTrigger<EntityAddedTrigger.Instance> {
    private static final MapCodec<Instance> CODEC = Codec.INT.xmap(Instance::new, i -> i.entityId).fieldOf("entity");
    private static final StreamCodec<? super RegistryFriendlyByteBuf, Instance> STREAM_CODEC = ByteBufCodecs.INT.map(Instance::new, i -> i.entityId);

    private final Multimap<Integer, Listener<Instance>> instances = HashMultimap.create();

    public static TriggerInstance forEntity(Entity entity) {
        return new Instance(entity.getId());
    }

    @Override
    public void addListener(Listener<Instance> instance) {
        instances.put(instance.getTrigger().entityId, instance);
    }

    @Override
    public void removeListener(Listener<Instance> instance) {
        instances.remove(instance.getTrigger().entityId, instance);
    }

    @Override
    public boolean active(Listener<Instance> instance) {
        return Objects.requireNonNull(Minecraft.getInstance().level).getEntity(instance.getTrigger().entityId) != null;
    }

    @Override
    public MapCodec<Instance> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, Instance> streamCodec() {
        return STREAM_CODEC;
    }

    public void trigger(int id) {
        instances.get(id).forEach(Listener::run);
        instances.removeAll(id);
    }

    public static class Instance implements TriggerInstance {
        private final int entityId;

        public Instance(int entityId) {
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
