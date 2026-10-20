package net.kapitencraft.kap_lib.core.util;

import net.minecraft.resources.ResourceLocation;

/**
 * base locations for attributes.
 * <br>using these as the location of a modifier on an item will cause the modifier to shown in green
 */
public class VanillaBaseAttributeLocations {
    public static final ResourceLocation LUCK = ResourceLocation.withDefaultNamespace("base_luck");
    public static final ResourceLocation BLOCK_BREAK_SPEED = ResourceLocation.withDefaultNamespace("base_block_break_speed");
    public static final ResourceLocation BLOCK_INTERACTION_RANGE = ResourceLocation.withDefaultNamespace("base_block_interaction_range");
    public static final ResourceLocation ENTITY_INTERACTION_RANGE = ResourceLocation.withDefaultNamespace("base_entity_interaction_range");
    public static final ResourceLocation MINING_EFFICIENCY = ResourceLocation.withDefaultNamespace("base_mining_efficiency");
    public static final ResourceLocation ATTACK_KNOCKBACK = ResourceLocation.withDefaultNamespace("base_attack_knockback");
}
