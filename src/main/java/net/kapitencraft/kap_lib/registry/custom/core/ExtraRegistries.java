package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.VisibleSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.AnimationTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public interface ExtraRegistries {
    IForgeRegistry<OverlayProperties> OVERLAY_PROPERTIES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.OVERLAY_PROPERTIES);
    IForgeRegistry<GlyphEffect> GLYPH_EFFECTS = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.GLYPH_EFFECTS);
    IForgeRegistry<IRequestable<?, ?>> REQUESTABLES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.REQUESTABLES);
    IForgeRegistry<DataPackSerializer<? extends ReqCondition<?>>> REQUIREMENT_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.REQ_CONDITIONS);
    IForgeRegistry<DataPackSerializer<? extends Bonus<?>>> BONUS_SERIALIZER = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.SET_BONUSES);
    IForgeRegistry<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.ATTRIBUTE_MODIFIER_TYPES);
    IForgeRegistry<Codec<? extends ComponentContents>> COMPONENT_CONTENT_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.COMPONENT_CONTENTS_TYPES);
    IForgeRegistry<Codec<? extends DataSource>> DATA_SOURCE_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.DATA_SOURCE_TYPES);
    IForgeRegistry<AnimationElement.Type<?>> ANIMATION_ELEMENT_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.MODIFICATION_ELEMENT_TYPES);
    IForgeRegistry<Spawner.Type<?>> SPAWN_ELEMENT_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.SPAWN_ELEMENT_TYPES);
    IForgeRegistry<ParticleFinalizer.Type<?>> PARTICLE_FINALIZER_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.FINALIZER_TYPES);
    IForgeRegistry<AnimationTerminator.Type<?>> ANIMATION_TERMINATOR_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.TERMINATOR_TYPES);
    IForgeRegistry<ActivationTrigger<?>> ACTIVATION_TRIGGERS = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.ACTIVATION_TRIGGERS);
}
