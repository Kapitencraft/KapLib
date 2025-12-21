package net.kapitencraft.kap_lib.attribute.mixin.classes;

import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
import net.kapitencraft.kap_lib.attribute.BaseAttributeLocations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.extensions.IAttributeExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Attribute.class)
public abstract class AttributeMixin implements IAttributeExtension, MixinSelfProvider<Attribute> {

    @Override
    public @Nullable ResourceLocation getBaseId() {
        if (self() == Attributes.LUCK.value()) return BaseAttributeLocations.LUCK;
        else if (self() == Attributes.BLOCK_BREAK_SPEED.value()) return BaseAttributeLocations.BLOCK_BREAK_SPEED;
        else if (self() == Attributes.BLOCK_INTERACTION_RANGE.value()) return BaseAttributeLocations.BLOCK_INTERACTION_RANGE;
        else if (self() == Attributes.ENTITY_INTERACTION_RANGE.value()) return BaseAttributeLocations.ENTITY_INTERACTION_RANGE;
        else if (self() == Attributes.MINING_EFFICIENCY.value()) return BaseAttributeLocations.MINING_EFFICIENCY;
        else if (self() == Attributes.ATTACK_KNOCKBACK.value()) return BaseAttributeLocations.ATTACK_KNOCKBACK;
        return IAttributeExtension.super.getBaseId();
    }

    @Override
    public Attribute self() {
        return MixinSelfProvider.super.self();
    }
}
