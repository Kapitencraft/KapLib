package net.kapitencraft.kap_lib.util.attribute;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

@EventBusSubscriber(modid = KapLibMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class AttributeAdder {
    @SubscribeEvent
    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        addAll(event, ExtraAttributes.STRENGTH.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.CRIT_DAMAGE.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.RANGED_DAMAGE.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.ARROW_COUNT.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.MAGIC_DEFENCE.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.TRUE_DEFENCE.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.BONUS_ATTACK_SPEED.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.CRIT_CHANCE.get(), ONLY_WITH_BRAIN);
        addAll(event, ExtraAttributes.COOLDOWN_REDUCTION.get(), LIVINGS);
        addToPlayer(event,
                ExtraAttributes.MINING_FORTUNE,
                ExtraAttributes.PRISTINE,
                ExtraAttributes.MINING_SPEED,
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
                ExtraAttributes.FISHING_SPEED
        );
    }

    private interface isAInstance {
        boolean is(EntityType<? extends LivingEntity> entityType);
    }

    private static final isAInstance ONLY_WITH_BRAIN = (entityType)-> (entityType.getCategory() != MobCategory.MISC) || entityType == EntityType.PLAYER;
    private static final isAInstance LIVINGS = entityType -> true;


    @SafeVarargs
    private static void addToPlayer(EntityAttributeModificationEvent event, Supplier<Attribute>... attributes) {
        Arrays.stream(attributes).map(Supplier::get).forEach(attribute -> event.add(EntityType.PLAYER, attribute));
    }


    private static void addAll(EntityAttributeModificationEvent event, Attribute attribute, isAInstance generator) {
        ForgeRegistries.ENTITY_TYPES.getValues().stream().map(AttributeAdder::toLiving).filter(Objects::nonNull).filter(generator::is)
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
