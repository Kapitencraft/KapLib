package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.AnimationTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryType;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public interface ExtraRegistries {
    IForgeRegistry<OverlayProperties> OVERLAY_PROPERTIES = reg(Keys.OVERLAY_PROPERTIES);
    IForgeRegistry<GlyphEffect> GLYPH_EFFECTS = reg(Keys.GLYPH_EFFECTS);
    IForgeRegistry<IRequestable<?, ?>> REQUESTABLES = reg(Keys.REQUESTABLES);
    IForgeRegistry<DataPackSerializer<? extends ReqCondition<?>>> REQUIREMENT_TYPES = reg(Keys.REQ_CONDITIONS);
    IForgeRegistry<DataPackSerializer<? extends Bonus<?>>> BONUS_SERIALIZER = reg(Keys.BONUS_SERIALIZERS);
    IForgeRegistry<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = reg(Keys.ATTRIBUTE_MODIFIER_TYPES);
    IForgeRegistry<Codec<? extends ComponentContents>> COMPONENT_CONTENT_TYPES = reg(Keys.COMPONENT_CONTENTS_TYPES);
    IForgeRegistry<Codec<? extends DataSource>> DATA_SOURCE_TYPES = reg(Keys.DATA_SOURCE_TYPES);

    IForgeRegistry<AnimationElement.Type<?>> ANIMATION_ELEMENT_TYPES = reg(Keys.MODIFIER_TYPES);
    IForgeRegistry<Spawner.Type<?>> SPAWN_ELEMENT_TYPES = reg(Keys.SPAWNER_TYPES);
    IForgeRegistry<ParticleFinalizer.Type<?>> PARTICLE_FINALIZER_TYPES = reg(Keys.FINALIZER_TYPES);
    IForgeRegistry<AnimationTerminator.Type<?>> ANIMATION_TERMINATOR_TYPES = reg(Keys.TERMINATOR_TYPES);
    IForgeRegistry<ActivationTrigger<?>> ACTIVATION_TRIGGERS = reg(Keys.ACTIVATION_TRIGGERS);

    IForgeRegistry<Modifier.Type<?>> CAMERA_MODIFIERS = reg(Keys.CAMERA_MODIFIERS);

    IForgeRegistry<SpawnEntityFunctionType> SPAWN_FUNCTION_TYPES = reg(Keys.FUNCTION_TYPES);
    IForgeRegistry<SpawnPoolEntryType> SPAWN_POOL_ENTRY_TYPES = reg(Keys.POOL_ENTRY_TYPES);

    private static <T> IForgeRegistry<T> reg(ResourceKey<Registry<T>> key) {
        return RegistryManager.ACTIVE.getRegistry(key);
    }

    interface Keys {
        /**
         * default overlay properties. register inside {@link net.kapitencraft.kap_lib.event.custom.client.RegisterConfigurableOverlaysEvent#addOverlay(RegistryObject, Function) RegisterConfigurableOverlaysEvent#addOverlay}, to apply the overlay
         */
        ResourceKey<Registry<OverlayProperties>> OVERLAY_PROPERTIES = createRegistry("overlay_properties");
        ResourceKey<Registry<GlyphEffect>> GLYPH_EFFECTS = createRegistry("glyph_effects");
        ResourceKey<Registry<IRequestable<?, ?>>> REQUESTABLES = createRegistry("requestables");
        ResourceKey<Registry<DataPackSerializer<? extends ReqCondition<?>>>> REQ_CONDITIONS = createRegistry("requirement_conditions");
        ResourceKey<Registry<DataPackSerializer<? extends Bonus<?>>>> BONUS_SERIALIZERS = createRegistry("bonus_serializers");
        ResourceKey<Registry<Codec<? extends AttributeModifier>>> ATTRIBUTE_MODIFIER_TYPES = vanillaRegistry("attribute_modifier_types");
        ResourceKey<Registry<Codec<? extends ComponentContents>>> COMPONENT_CONTENTS_TYPES = vanillaRegistry("component_contents_types");
        /**
         * used to create codec. very unlikely that anyone finds an actual use for this
         */
        ResourceKey<Registry<Codec<? extends DataSource>>> DATA_SOURCE_TYPES = vanillaRegistry("data_source_types");

        //PARTICLE ANIMATION
        ResourceKey<Registry<AnimationElement.Type<?>>> MODIFIER_TYPES = createRegistry("particle_animation/element_types");
        ResourceKey<Registry<Spawner.Type<?>>> SPAWNER_TYPES = createRegistry("particle_animation/spawner_types");
        ResourceKey<Registry<ParticleFinalizer.Type<?>>> FINALIZER_TYPES = createRegistry("particle_animation/finalizer_types");
        ResourceKey<Registry<AnimationTerminator.Type<?>>> TERMINATOR_TYPES = createRegistry("particle_animation/terminator_types");
        ResourceKey<Registry<ActivationTrigger<?>>> ACTIVATION_TRIGGERS = createRegistry("particle_animation/activation_triggers");

        //CAMERA CONTROL
        ResourceKey<Registry<Modifier.Type<?>>> CAMERA_MODIFIERS = createRegistry("camera_modifiers");

        //SPAWN TABLE
        ResourceKey<Registry<SpawnEntityFunctionType>> FUNCTION_TYPES = createRegistry("spawn_table/function_types");
        ResourceKey<Registry<SpawnPoolEntryType>> POOL_ENTRY_TYPES = createRegistry("spawn_table/pool_entry_types");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(KapLibMod.res(id));
        }

        private static <T> ResourceKey<Registry<T>> vanillaRegistry(String id) {
            return ResourceKey.createRegistryKey(new ResourceLocation(id));
        }

    }
}
