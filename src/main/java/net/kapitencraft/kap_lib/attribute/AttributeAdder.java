package net.kapitencraft.kap_lib.attribute;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

@EventBusSubscriber(modid = LibConstants.MOD_ID)
public class AttributeAdder {
    @SubscribeEvent
    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        addAll(event, ExtraAttributes.STRENGTH, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.CRIT_DAMAGE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.RANGED_DAMAGE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.MAGIC_DEFENCE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.TRUE_DEFENCE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.BONUS_ATTACK_SPEED, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.CRIT_CHANCE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.VITALITY, ONLY_WITH_BRAIN);
        addToPlayer(event,
                ExtraAttributes.MINING_FORTUNE,
                ExtraAttributes.PRISTINE,
                ExtraAttributes.MAGIC_DAMAGE,
                ExtraAttributes.FEROCITY,
                ExtraAttributes.DODGE,
                ExtraAttributes.LIFE_STEAL,
                ExtraAttributes.DRAW_SPEED,
                ExtraAttributes.PROJECTILE_SPEED,
                ExtraAttributes.ARMOR_SHREDDER,
                ExtraAttributes.DOUBLE_JUMP,
                ExtraAttributes.FISHING_SPEED,
                ExtraAttributes.WISDOM
        );

    }

    private static final Predicate<EntityType<? extends LivingEntity>> ONLY_WITH_BRAIN = (entityType)-> (entityType.getCategory() != MobCategory.MISC) || entityType == EntityType.PLAYER;
    private static final Predicate<EntityType<? extends LivingEntity>> LIVINGS = entityType -> true;

    @SafeVarargs
    private static void addToPlayer(EntityAttributeModificationEvent event, Holder<Attribute>... attributes) {
        Arrays.stream(attributes).forEach(attribute -> event.add(EntityType.PLAYER, attribute));
    }

    private static void addAll(EntityAttributeModificationEvent event, Holder<Attribute> attribute, Predicate<EntityType<? extends LivingEntity>> generator) {
        BuiltInRegistries.ENTITY_TYPE.stream().map(AttributeAdder::toLiving).filter(Objects::nonNull).filter(generator)
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
