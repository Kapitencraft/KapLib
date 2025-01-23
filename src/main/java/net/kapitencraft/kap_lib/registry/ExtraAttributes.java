package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ExtraAttributes {
    DeferredRegister<Attribute> REGISTRY = KapLibMod.registry(ForgeRegistries.ATTRIBUTES);
    private static RegistryObject<Attribute> register(String name, double initValue, double minValue, double maxValue) {
        return REGISTRY.register("generic." + name, ()-> new RangedAttribute("generic." + name, initValue, minValue, maxValue).setSyncable(true));
    }

    private static RegistryObject<Attribute> register0Max(String name, double initValue) {
        return register(name, initValue, 0, Double.MAX_VALUE);
    }

    //Defensive Stats
    /**
     * chance to dodge attacks
     */
    RegistryObject<Attribute> DODGE = register("dodge", 0, 0, 100);
    /**
     * defence against magic attacks
     */
    RegistryObject<Attribute> MAGIC_DEFENCE = register0Max("magic_defence", 0);
    /**
     * defence against armor-piercing attacks
     */
    RegistryObject<Attribute> TRUE_DEFENCE = register0Max("true_defence", 0);
    /**
     * double jump. lets you jump in the air
     */
    RegistryObject<Attribute> DOUBLE_JUMP = register("double_jump", 0, 0, 20);
    /**
     * health regeneration scale
     */
    RegistryObject<Attribute> VITALITY = register0Max("vitality", 0);

    //Offensive Stats
    /**
     * reduces iFrames of hit enemies
     */
    RegistryObject<Attribute> BONUS_ATTACK_SPEED = register("bonus_attack_speed", 0, 0, 100);
    /**
     * increases melee and ranged damage
     */
    RegistryObject<Attribute> STRENGTH = register0Max("strength", 0);
    /**
     * increases critical damage
     */
    RegistryObject<Attribute> CRIT_DAMAGE = register0Max("crit_damage", 50);
    /**
     * gain a chance to crit regardless of vanilla crit behaviour
     */
    RegistryObject<Attribute> CRIT_CHANCE = register("crit_chance", 0, 0, 100);
    /**
     * gain a chance to re-attack delayed
     */
    RegistryObject<Attribute> FEROCITY = register("ferocity", 0, 0, 500);
    /**
     * increase maximum mana
     */
    RegistryObject<Attribute> INTELLIGENCE = register0Max("intel", 0);
    /**
     * increases damage dealt by abilities
     */
    RegistryObject<Attribute> ABILITY_DAMAGE = register0Max("ability_damage", 0);
    /**
     * increases the damage arrows and other projectiles deal
     */
    RegistryObject<Attribute> RANGED_DAMAGE = register("ranged_damage", 0, 0, 100);
    /**
     * increases the amount of arrows shot per burst (similar to Multishot-Enchantment)
     */
    RegistryObject<Attribute> ARROW_COUNT = register("arrow_count", 0, 0, 100);
    /**
     * increases the speed a bow can be pulled
     */
    RegistryObject<Attribute> DRAW_SPEED = register("draw_speed", 100, 0, 1000);
    /**
     * increases the traveling speed of projectiles
     */
    RegistryObject<Attribute> PROJECTILE_SPEED = register("projectile_speed", 0, 0, 10000);

    //Mining
    /**
     * increases the speed of mining
     */
    RegistryObject<Attribute> MINING_SPEED = register0Max("mining_speed", 0);
    RegistryObject<Attribute> PRISTINE = register("pristine", 0, 0, 400);
    /**
     * increases the chance to get more drops from mining
     */
    RegistryObject<Attribute> MINING_FORTUNE = register0Max("mining_fortune", 0);

    //Misc
    RegistryObject<Attribute> COOLDOWN_REDUCTION = register("cooldown_reduction", 0, 0, 100);
    RegistryObject<Attribute> LIVE_STEAL = register("live_steal", 0, 0, 10);
    RegistryObject<Attribute> ARMOR_SHREDDER = register("armor_shredder", 0, 0, 100);
    RegistryObject<Attribute> FISHING_SPEED = register0Max("fishing_speed", 0);

    //Mana
    RegistryObject<Attribute> MAX_MANA = register0Max("max_mana", 100);
    RegistryObject<Attribute> MANA = register0Max("mana", 100);
    RegistryObject<Attribute> MANA_COST = register("mana_cost", 0, 0, 100000);
    RegistryObject<Attribute> MANA_REGEN = register0Max("mana_regen", 0);
}