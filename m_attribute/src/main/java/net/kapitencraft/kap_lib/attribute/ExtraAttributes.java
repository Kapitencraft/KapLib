package net.kapitencraft.kap_lib.attribute;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.util.Modules;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

/**
 * joined extra attributes that might be useful
 */
public interface ExtraAttributes {
    DeferredRegister<Attribute> REGISTRY = LibConstants.registry(Registries.ATTRIBUTE);
    private static Holder<Attribute> register(String name, double initValue, double minValue, double maxValue, @Nullable ResourceLocation baseLocation) {
        return REGISTRY.register("generic." + name, ()-> new RangedAttribute("generic." + name, initValue, minValue, maxValue) {
            @Override
            public @Nullable ResourceLocation getBaseId() {
                return baseLocation;
            }
        }.setSyncable(true));
    }

    @SuppressWarnings("unused")
    private static Holder<Attribute> registerNegative(String name, double initValue, double minValue, double maxValue, ResourceLocation baseId) {
        return REGISTRY.register("generic." + name, () -> new RangedAttribute("generic." + name, initValue, minValue, maxValue) {
            @Override
            public @Nullable ResourceLocation getBaseId() {
                return baseId;
            }
        }.setSentiment(Attribute.Sentiment.NEGATIVE).setSyncable(true));
    }

    private static Holder<Attribute> register0Max(String name, double initValue, ResourceLocation baseLocation) {
        return register(name, initValue, 0, Double.MAX_VALUE, baseLocation);
    }

    //region Defensive
    /**
     * chance to dodge attacks
     */
    Holder<Attribute> DODGE = register("dodge", 0, 0, 100, null);
    /**
     * defence against magic attacks
     */
    Holder<Attribute> MAGIC_DEFENCE = register0Max("magic_defence", 0, null);
    /**
     * defence against armor-piercing attacks
     */
    Holder<Attribute> TRUE_DEFENCE = register0Max("true_defence", 0, null);
    /**
     * double jump. lets you jump in the air
     */
    Holder<Attribute> DOUBLE_JUMP = register("double_jump", 0, 0, 20, null);
    /**
     * health regeneration scale
     */
    Holder<Attribute> VITALITY = register0Max("vitality", 0, null);
    //endregion

    //region Offensive
    /**
     * reduces iFrames of hit enemies
     */
    Holder<Attribute> BONUS_ATTACK_SPEED = register("bonus_attack_speed", 0, 0, 100, BaseAttributeLocations.BONUS_ATTACK_SPEED);
    /**
     * increases melee and ranged damage
     */
    Holder<Attribute> STRENGTH = register0Max("strength", 0, BaseAttributeLocations.STRENGTH);
    /**
     * increases critical damage
     */
    Holder<Attribute> CRIT_DAMAGE = register0Max("crit_damage", 50, BaseAttributeLocations.CRIT_DAMAGE);
    /**
     * gain a chance to crit regardless of vanilla crit behaviour
     */
    Holder<Attribute> CRIT_CHANCE = register("crit_chance", 0, 0, 100, BaseAttributeLocations.CRIT_CHANCE);
    /**
     * gain a chance to re-attack delayed
     */
    Holder<Attribute> FEROCITY = register("ferocity", 0, 0, 500, BaseAttributeLocations.FEROCITY);
    /**
     * increases damage dealt by abilities
     */
    Holder<Attribute> MAGIC_DAMAGE = register0Max("magic_damage", 0, BaseAttributeLocations.MAGIC_DAMAGE);
    /**
     * increases the damage arrows and other projectiles deal
     */
    Holder<Attribute> RANGED_DAMAGE = register("ranged_damage", 0, 0, 100, BaseAttributeLocations.RANGED_DAMAGE);
    /**
     * increases the speed a bow can be pulled
     */
    Holder<Attribute> DRAW_SPEED = register("draw_speed", 100, 0, 1000, BaseAttributeLocations.DRAW_SPEED);
    /**
     * increases the traveling speed of projectiles
     */
    Holder<Attribute> PROJECTILE_SPEED = register("projectile_speed", 0, 0, 10000, BaseAttributeLocations.PROJECTILE_SPEED);
    //endregion

    //region Mining
    Holder<Attribute> PRISTINE = register("pristine", 0, 0, 400, BaseAttributeLocations.PRISTINE);
    Holder<Attribute> MINING_FORTUNE = register0Max("mining_fortune", 0, BaseAttributeLocations.MINING_FORTUNE);
    //endregion

    //Misc
    /**
     * amount of health regenerated when attacking
     */
    Holder<Attribute> LIFE_STEAL = register("life_steal", 0, 0, 10, BaseAttributeLocations.LIVE_STEAL);
    /**
     * amount of armor ignored by attacks
     */
    Holder<Attribute> ARMOR_SHREDDER = register("armor_shredder", 0, 0, 100, BaseAttributeLocations.ARMOR_SHREDDER);
    /**
     * defines how quick fishing is. values above 600 instantly attract fish
     */
    Holder<Attribute> FISHING_SPEED = register("fishing_speed", 0, 0, 600, BaseAttributeLocations.FISHING_SPEED);
    /**
     * increases experience gained from mining and combat
     */
    Holder<Attribute> WISDOM = register("wisdom", 0, -100, 10000, BaseAttributeLocations.WISDOM);

    /**
     * gets the players experience scale, which should be multiplied with the base experience to get the final dropped experience
     */
    static double getExperienceScale(Player player) {
        if (!Modules.isAttributesActive()) {
            return 1;
        }
        double wisdom = player.getAttributeValue(ExtraAttributes.WISDOM);
        return 1 + (wisdom / 100);
    }
}