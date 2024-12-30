package net.kapitencraft.kap_lib.registry.custom;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.kapitencraft.kap_lib.util.attribute.TimedModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface AttributeModifierTypes {

    DeferredRegister<Codec<? extends AttributeModifier>> REGISTRY = KapLibMod.registry(ExtraRegistryKeys.ATTRIBUTE_MODIFIER_TYPES);

    RegistryObject<Codec<? extends AttributeModifier>> TIMED_MODIFIER = REGISTRY.register("timed", () -> TimedModifier.CODEC);
}
