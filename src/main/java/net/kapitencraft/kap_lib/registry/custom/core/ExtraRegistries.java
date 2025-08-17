package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.core.TerminationTrigger;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.inventory.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryType;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ExtraRegistries {
    Registry<OverlayProperties> OVERLAY_PROPERTIES = reg(Keys.OVERLAY_PROPERTIES);
    Registry<GlyphEffect> GLYPH_EFFECTS = reg(Keys.GLYPH_EFFECTS);
    Registry<DataPackSerializer<? extends ReqCondition<?>>> REQUIREMENT_TYPES = reg(Keys.REQ_CONDITIONS);
    Registry<RegistrySerializer<? extends Bonus<?>>> BONUS_SERIALIZER = reg(Keys.BONUS_SERIALIZERS);
    Registry<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = reg(Keys.ATTRIBUTE_MODIFIER_TYPES);
    Registry<ComponentContents.Type<?>> COMPONENT_CONTENT_TYPES = reg(Keys.COMPONENT_CONTENTS_TYPES);
    Registry<Codec<? extends DataSource>> DATA_SOURCE_TYPES = reg(Keys.DATA_SOURCE_TYPES);

    Registry<AnimationElement.Type<?>> ANIMATION_ELEMENT_TYPES = reg(Keys.MODIFIER_TYPES);
    Registry<Spawner.Type<?>> SPAWN_ELEMENT_TYPES = reg(Keys.SPAWNER_TYPES);
    Registry<ParticleFinalizer.Type<?>> PARTICLE_FINALIZER_TYPES = reg(Keys.FINALIZER_TYPES);
    Registry<TerminationTrigger<?>> TERMINATION_TRIGGERS = reg(Keys.TERMINATOR_TYPES);
    Registry<ActivationTrigger<?>> ACTIVATION_TRIGGERS = reg(Keys.ACTIVATION_TRIGGERS);

    Registry<Modifier.Type<?>> CAMERA_MODIFIERS = reg(Keys.CAMERA_MODIFIERS);

    Registry<SpawnEntityFunctionType<?>> SPAWN_FUNCTION_TYPES = reg(Keys.FUNCTION_TYPES);
    Registry<SpawnPoolEntryType> SPAWN_POOL_ENTRY_TYPES = reg(Keys.POOL_ENTRY_TYPES);

    Registry<WearableSlot> WEARABLE_SLOTS = reg(Keys.WEARABLE_SLOTS);
    Registry<InventoryPageType<?>> INVENTORY_PAGES = reg(Keys.INVENTORY_PAGES);

    Registry<Cooldown> COOLDOWNS = reg(Keys.COOLDOWNS);

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    private static <T> Registry<T> reg(ResourceKey<Registry<T>> key) {
        Registry<T> registry = new RegistryBuilder<>(key).create();
        registries.add(registry);
        return registry;
    }

    @ApiStatus.Internal
    static void registerAll(Consumer<Registry<?>> register) {
        registries.forEach(register);
    }


    interface Keys {
        /**
         * default overlay properties. register inside {@link net.kapitencraft.kap_lib.event.custom.client.RegisterConfigurableOverlaysEvent#addOverlay(net.minecraft.core.Holder, Function) RegisterConfigurableOverlaysEvent#addOverlay}, to apply the overlay
         */
        ResourceKey<Registry<OverlayProperties>> OVERLAY_PROPERTIES = createRegistry("overlay_properties");
        ResourceKey<Registry<GlyphEffect>> GLYPH_EFFECTS = createRegistry("glyph_effects");
        ResourceKey<Registry<DataPackSerializer<? extends ReqCondition<?>>>> REQ_CONDITIONS = createRegistry("requirement_conditions");
        ResourceKey<Registry<RegistrySerializer<? extends Bonus<?>>>> BONUS_SERIALIZERS = createRegistry("bonus_serializers");
        ResourceKey<Registry<Codec<? extends AttributeModifier>>> ATTRIBUTE_MODIFIER_TYPES = vanillaRegistry("attribute_modifier_types");
        ResourceKey<Registry<ComponentContents.Type<?>>> COMPONENT_CONTENTS_TYPES = vanillaRegistry("component_contents_types");
        /**
         * used to create codec. very unlikely that anyone finds an actual use for this
         */
        ResourceKey<Registry<Codec<? extends DataSource>>> DATA_SOURCE_TYPES = vanillaRegistry("data_source_types");

        //PARTICLE ANIMATION
        ResourceKey<Registry<AnimationElement.Type<?>>> MODIFIER_TYPES = createRegistry("particle_animation/element_types");
        ResourceKey<Registry<Spawner.Type<?>>> SPAWNER_TYPES = createRegistry("particle_animation/spawner_types");
        ResourceKey<Registry<ParticleFinalizer.Type<?>>> FINALIZER_TYPES = createRegistry("particle_animation/finalizer_types");
        ResourceKey<Registry<TerminationTrigger<?>>> TERMINATOR_TYPES = createRegistry("particle_animation/terminator_types");
        ResourceKey<Registry<ActivationTrigger<?>>> ACTIVATION_TRIGGERS = createRegistry("particle_animation/activation_triggers");

        //CAMERA CONTROL
        ResourceKey<Registry<Modifier.Type<?>>> CAMERA_MODIFIERS = createRegistry("camera_modifiers");

        //SPAWN TABLE
        ResourceKey<Registry<SpawnEntityFunctionType<?>>> FUNCTION_TYPES = createRegistry("spawn_table/function_types");
        ResourceKey<Registry<SpawnPoolEntryType>> POOL_ENTRY_TYPES = createRegistry("spawn_table/pool_entry_types");

        ResourceKey<Registry<WearableSlot>> WEARABLE_SLOTS = createRegistry("wearable_slots");

        ResourceKey<Registry<InventoryPageType<?>>> INVENTORY_PAGES = createRegistry("inventory_pages");

        ResourceKey<Registry<Cooldown>> COOLDOWNS = createRegistry("cooldowns");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(KapLibMod.res(id));
        }

        private static <T> ResourceKey<Registry<T>> vanillaRegistry(String id) {
            return ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace(id));
        }

    }
}
