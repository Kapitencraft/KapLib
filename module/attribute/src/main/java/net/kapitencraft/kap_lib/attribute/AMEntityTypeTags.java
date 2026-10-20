package net.kapitencraft.kap_lib.attribute;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public interface AMEntityTypeTags {
    TagKey<EntityType<?>> IMMUNE_TO_LIFE_STEAL = TagKey.create(Registries.ENTITY_TYPE, LibConstants.res("immune_to_life_steal"));
}