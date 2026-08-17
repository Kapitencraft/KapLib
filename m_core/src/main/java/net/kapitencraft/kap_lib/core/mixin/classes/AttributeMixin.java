package net.kapitencraft.kap_lib.core.mixin.classes;

import net.kapitencraft.kap_lib.core.util.VanillaBaseAttributeLocations;
import net.kapitencraft.kap_lib.core.mixin.duck.MixinSelfProvider;
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
        if (self() == Attributes.LUCK.value()) return VanillaBaseAttributeLocations.LUCK;
        else if (self() == Attributes.BLOCK_BREAK_SPEED.value()) return VanillaBaseAttributeLocations.BLOCK_BREAK_SPEED;
        else if (self() == Attributes.BLOCK_INTERACTION_RANGE.value())
            return VanillaBaseAttributeLocations.BLOCK_INTERACTION_RANGE;
        else if (self() == Attributes.ENTITY_INTERACTION_RANGE.value())
            return VanillaBaseAttributeLocations.ENTITY_INTERACTION_RANGE;
        else if (self() == Attributes.MINING_EFFICIENCY.value()) return VanillaBaseAttributeLocations.MINING_EFFICIENCY;
        else if (self() == Attributes.ATTACK_KNOCKBACK.value()) return VanillaBaseAttributeLocations.ATTACK_KNOCKBACK;
        return IAttributeExtension.super.getBaseId();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public Attribute self() {
        return MixinSelfProvider.super.self();
    }
}
