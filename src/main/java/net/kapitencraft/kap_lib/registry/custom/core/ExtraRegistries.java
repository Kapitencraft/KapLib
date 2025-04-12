package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.AnimationTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryType;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public interface ExtraRegistries {
    IForgeRegistry<OverlayProperties> OVERLAY_PROPERTIES = reg(ExtraRegistryKeys.OVERLAY_PROPERTIES);
    IForgeRegistry<GlyphEffect> GLYPH_EFFECTS = reg(ExtraRegistryKeys.GLYPH_EFFECTS);
    IForgeRegistry<IRequestable<?, ?>> REQUESTABLES = reg(ExtraRegistryKeys.REQUESTABLES);
    IForgeRegistry<DataPackSerializer<? extends ReqCondition<?>>> REQUIREMENT_TYPES = reg(ExtraRegistryKeys.REQ_CONDITIONS);
    IForgeRegistry<DataPackSerializer<? extends Bonus<?>>> BONUS_SERIALIZER = reg(ExtraRegistryKeys.SET_BONUSES);
    IForgeRegistry<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = reg(ExtraRegistryKeys.ATTRIBUTE_MODIFIER_TYPES);
    IForgeRegistry<Codec<? extends ComponentContents>> COMPONENT_CONTENT_TYPES = reg(ExtraRegistryKeys.COMPONENT_CONTENTS_TYPES);
    IForgeRegistry<Codec<? extends DataSource>> DATA_SOURCE_TYPES = reg(ExtraRegistryKeys.DATA_SOURCE_TYPES);

    IForgeRegistry<AnimationElement.Type<?>> ANIMATION_ELEMENT_TYPES = reg(ExtraRegistryKeys.MODIFIER_TYPES);
    IForgeRegistry<Spawner.Type<?>> SPAWN_ELEMENT_TYPES = reg(ExtraRegistryKeys.SPAWNER_TYPES);
    IForgeRegistry<ParticleFinalizer.Type<?>> PARTICLE_FINALIZER_TYPES = reg(ExtraRegistryKeys.FINALIZER_TYPES);
    IForgeRegistry<AnimationTerminator.Type<?>> ANIMATION_TERMINATOR_TYPES = reg(ExtraRegistryKeys.TERMINATOR_TYPES);
    IForgeRegistry<ActivationTrigger<?>> ACTIVATION_TRIGGERS = reg(ExtraRegistryKeys.ACTIVATION_TRIGGERS);

    IForgeRegistry<Modifier.Type<?>> CAMERA_MODIFIERS = reg(ExtraRegistryKeys.CAMERA_MODIFIERS);

    IForgeRegistry<SpawnEntityFunctionType> SPAWN_FUNCTION_TYPES = reg(ExtraRegistryKeys.FUNCTION_TYPES);
    IForgeRegistry<SpawnPoolEntryType> SPAWN_POOL_ENTRY_TYPES = reg(ExtraRegistryKeys.POOL_ENTRY_TYPES);

    private static <T> IForgeRegistry<T> reg(ResourceKey<Registry<T>> key) {
        return RegistryManager.ACTIVE.getRegistry(key);
    }
}
