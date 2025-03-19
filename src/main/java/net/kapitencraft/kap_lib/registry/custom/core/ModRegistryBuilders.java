package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.client.cam.rot.Rotator;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.AnimationTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.VisibleSpawner;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ModRegistryBuilders {

    RegistryBuilder<OverlayProperties> OVERLAY_PROPERTIES = makeBuilder(ExtraRegistryKeys.OVERLAY_PROPERTIES);
    RegistryBuilder<GlyphEffect> GLYPH_EFFECTS = makeBuilder(ExtraRegistryKeys.GLYPH_EFFECTS);
    RegistryBuilder<IRequestable<?, ?>> REQUESTABLES_BUILDER = makeBuilder(ExtraRegistryKeys.REQUESTABLES);
    RegistryBuilder<DataPackSerializer<? extends ReqCondition<?>>> REQUIREMENTS_BUILDER = makeBuilder(ExtraRegistryKeys.REQ_CONDITIONS);
    RegistryBuilder<DataPackSerializer<? extends Bonus<?>>> SET_BONUSES = makeBuilder(ExtraRegistryKeys.SET_BONUSES);
    RegistryBuilder<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = makeBuilder(ExtraRegistryKeys.ATTRIBUTE_MODIFIER_TYPES);
    RegistryBuilder<Codec<? extends ComponentContents>> COMPONENT_CONTENTS_TYPES = makeBuilder(ExtraRegistryKeys.COMPONENT_CONTENTS_TYPES);
    RegistryBuilder<Codec<? extends DataSource>> DATA_SOURCE_TYPES = makeBuilder(ExtraRegistryKeys.DATA_SOURCE_TYPES);
    RegistryBuilder<AnimationElement.Type<?>> ANIMATION_ELEMENT_TYPES = makeBuilder(ExtraRegistryKeys.MODIFICATION_ELEMENT_TYPES);
    RegistryBuilder<Spawner.Type<?>> SPAWN_ELEMENT_TYPES = makeBuilder(ExtraRegistryKeys.SPAWN_ELEMENT_TYPES);
    RegistryBuilder<AnimationTerminator.Type<?>> ANIMATION_TERMINATOR_TYPES = makeBuilder(ExtraRegistryKeys.TERMINATOR_TYPES);
    RegistryBuilder<ParticleFinalizer.Type<?>> PARTICLE_FINALIZER_TYPES = makeBuilder(ExtraRegistryKeys.FINALIZER_TYPES);
    RegistryBuilder<ActivationTrigger<?>> ACTIVATION_LISTENER_TYPES = makeBuilder(ExtraRegistryKeys.ACTIVATION_TRIGGERS);
    RegistryBuilder<Rotator.Type<?>> CAMERA_ROTATORS = makeBuilder(ExtraRegistryKeys.CAMERA_ROTATORS);

    private static <T> RegistryBuilder<T> makeBuilder(ResourceKey<Registry<T>> location) {
        return new RegistryBuilder<T>().setName(location.location());
    }
}
