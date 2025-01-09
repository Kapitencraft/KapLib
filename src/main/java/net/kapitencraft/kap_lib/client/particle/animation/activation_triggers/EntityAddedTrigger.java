package net.kapitencraft.kap_lib.client.particle.animation.activation_triggers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.TriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ActivationTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import java.util.Objects;

public class EntityAddedTrigger implements ActivationTrigger<EntityAddedTrigger.Instance> {
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
    public void toNw(FriendlyByteBuf buf, Instance val) {
        buf.writeInt(val.entityId);
    }

    @Override
    public Instance fromNw(FriendlyByteBuf buf) {
        return new Instance(buf.readInt());
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
