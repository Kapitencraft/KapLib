package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.cam.rot.Rotator;
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
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;


public interface ExtraRegistryKeys {

    /**
     * default overlay properties. register inside {@link net.kapitencraft.kap_lib.event.custom.client.RegisterConfigurableOverlaysEvent#addOverlay(RegistryObject, Function) RegisterConfigurableOverlaysEvent#addOverlay}, to apply the overlay
     */
    ResourceKey<Registry<OverlayProperties>> OVERLAY_PROPERTIES = createRegistry("overlay_properties");
    ResourceKey<Registry<GlyphEffect>> GLYPH_EFFECTS = createRegistry("glyph_effects");
    ResourceKey<Registry<IRequestable<?, ?>>> REQUESTABLES = createRegistry("requestables");
    ResourceKey<Registry<DataPackSerializer<? extends ReqCondition<?>>>> REQ_CONDITIONS = createRegistry("requirement_conditions");
    ResourceKey<Registry<DataPackSerializer<? extends Bonus<?>>>> SET_BONUSES = createRegistry("set_bonuses");
    ResourceKey<Registry<Codec<? extends AttributeModifier>>> ATTRIBUTE_MODIFIER_TYPES = vanillaRegistry("attribute_modifier_types");
    ResourceKey<Registry<Codec<? extends ComponentContents>>> COMPONENT_CONTENTS_TYPES = vanillaRegistry("component_contents_types");
    /**
     * used to create codec. very unlikely that anyone finds an actual use for this
     */
    ResourceKey<Registry<Codec<? extends DataSource>>> DATA_SOURCE_TYPES = vanillaRegistry("data_source_types");

    //PARTICLE ANIMATION
    ResourceKey<Registry<AnimationElement.Type<?>>> MODIFICATION_ELEMENT_TYPES = createRegistry("particle_animation/element_types");
    ResourceKey<Registry<Spawner.Type<?>>> SPAWN_ELEMENT_TYPES = createRegistry("particle_animation/spawn_types");
    ResourceKey<Registry<ParticleFinalizer.Type<?>>> FINALIZER_TYPES = createRegistry("particle_animation/finalizer_types");
    ResourceKey<Registry<AnimationTerminator.Type<?>>> TERMINATOR_TYPES = createRegistry("particle_animation/terminator_types");
    ResourceKey<Registry<ActivationTrigger<?>>> ACTIVATION_TRIGGERS = createRegistry("particle_animation/activation_triggers");

    //CAMERA CONTROL
    ResourceKey<Registry<Rotator.Type<?>>> CAMERA_ROTATORS = createRegistry("camera/rotators");

    private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
        return ResourceKey.createRegistryKey(KapLibMod.res(id));
    }

    private static <T> ResourceKey<Registry<T>> vanillaRegistry(String id) {
        return ResourceKey.createRegistryKey(new ResourceLocation(id));
    }
}
