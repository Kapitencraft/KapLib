package net.kapitencraft.kap_lib.cooldown;

import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttributes;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

import java.util.Objects;
import java.util.function.Predicate;

public class CooldownAttributeAdder {

    public static void addAttributes(EntityAttributeModificationEvent event) {
        addAll(event, CooldownAttributes.COOLDOWN_REDUCTION, LIVINGS);
    }

    private static final Predicate<EntityType<? extends LivingEntity>> LIVINGS = entityType -> true;

    private static void addAll(EntityAttributeModificationEvent event, Holder<Attribute> attribute, Predicate<EntityType<? extends LivingEntity>> generator) {
        BuiltInRegistries.ENTITY_TYPE.stream().map(CooldownAttributeAdder::toLiving).filter(Objects::nonNull).filter(generator)
                .forEach(entityType -> event.add(entityType, attribute));
    }

    private static EntityType<? extends LivingEntity> toLiving(EntityType<?> in) {
        try {
            return (EntityType<? extends LivingEntity>) in;
        } catch (ClassCastException e) {
            return null;
        }
    }
}
