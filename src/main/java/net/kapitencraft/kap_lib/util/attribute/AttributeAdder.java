package net.kapitencraft.kap_lib.util.attribute;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
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
import java.util.function.Supplier;

@EventBusSubscriber(modid = KapLibMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AttributeAdder {
    @SubscribeEvent
    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        addAll(event, ExtraAttributes.STRENGTH, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.CRIT_DAMAGE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.RANGED_DAMAGE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.ARROW_COUNT, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.MAGIC_DEFENCE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.TRUE_DEFENCE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.BONUS_ATTACK_SPEED, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.CRIT_CHANCE, ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.COOLDOWN_REDUCTION, LIVINGS);
        addAll(event, ExtraAttributes.VITALITY, ONLY_WITH_BRAIN);
        addToPlayer(event,
                ExtraAttributes.MINING_FORTUNE,
                ExtraAttributes.PRISTINE,
                ExtraAttributes.ABILITY_DAMAGE,
                ExtraAttributes.MANA_COST,
                ExtraAttributes.INTELLIGENCE,
                ExtraAttributes.FEROCITY,
                ExtraAttributes.MAX_MANA,
                ExtraAttributes.MANA_REGEN,
                ExtraAttributes.MANA,
                ExtraAttributes.DODGE,
                ExtraAttributes.LIVE_STEAL,
                ExtraAttributes.DRAW_SPEED,
                ExtraAttributes.PROJECTILE_SPEED,
                ExtraAttributes.ARMOR_SHREDDER,
                ExtraAttributes.DOUBLE_JUMP,
                ExtraAttributes.FISHING_SPEED,
                ExtraAttributes.WISDOM
        );
    }

    private interface isAInstance {
        boolean is(EntityType<? extends LivingEntity> entityType);
    }

    private static final isAInstance ONLY_WITH_BRAIN = (entityType)-> (entityType.getCategory() != MobCategory.MISC) || entityType == EntityType.PLAYER;
    private static final isAInstance LIVINGS = entityType -> true;


    @SafeVarargs
    private static void addToPlayer(EntityAttributeModificationEvent event, Holder<Attribute>... attributes) {
        Arrays.stream(attributes).forEach(attribute -> event.add(EntityType.PLAYER, attribute));
    }


    private static void addAll(EntityAttributeModificationEvent event, Holder<Attribute> attribute, isAInstance generator) {
        BuiltInRegistries.ENTITY_TYPE.stream().map(AttributeAdder::toLiving).filter(Objects::nonNull).filter(generator::is)
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
