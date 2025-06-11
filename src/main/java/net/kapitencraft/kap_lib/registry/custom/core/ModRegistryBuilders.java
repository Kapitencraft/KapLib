package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.client.cam.modifiers.Modifier;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.overlay.OverlayProperties;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.ActivationTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.Spawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.AnimationTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.ParticleFinalizer;
import net.kapitencraft.kap_lib.inventory.page.InventoryPageType;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.kapitencraft.kap_lib.spawn_table.entries.SpawnPoolEntryType;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * mod registry builders.
 * <br>add callbacks by hooking up to the {@link NewRegistryEvent} with a high priority listener
 */
@SuppressWarnings("unused")
public interface ModRegistryBuilders {
    /**
     * builders list. only used to register the registries. no need to use this yourself
     */
    @ApiStatus.Internal
    List<RegistryBuilder<?>> builders = new ArrayList<>();

    RegistryBuilder<OverlayProperties> OVERLAY_PROPERTIES = makeBuilder(ExtraRegistries.Keys.OVERLAY_PROPERTIES);
    RegistryBuilder<GlyphEffect> GLYPH_EFFECTS = makeBuilder(ExtraRegistries.Keys.GLYPH_EFFECTS);
    RegistryBuilder<IRequestable<?, ?>> REQUESTABLES_BUILDER = makeBuilder(ExtraRegistries.Keys.REQUESTABLES);
    RegistryBuilder<DataPackSerializer<? extends ReqCondition<?>>> REQUIREMENTS_BUILDER = makeBuilder(ExtraRegistries.Keys.REQ_CONDITIONS);
    RegistryBuilder<DataPackSerializer<? extends Bonus<?>>> SET_BONUSES = makeBuilder(ExtraRegistries.Keys.BONUS_SERIALIZERS);
    RegistryBuilder<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = makeBuilder(ExtraRegistries.Keys.ATTRIBUTE_MODIFIER_TYPES);
    RegistryBuilder<Codec<? extends ComponentContents>> COMPONENT_CONTENTS_TYPES = makeBuilder(ExtraRegistries.Keys.COMPONENT_CONTENTS_TYPES);
    RegistryBuilder<Codec<? extends DataSource>> DATA_SOURCE_TYPES = makeBuilder(ExtraRegistries.Keys.DATA_SOURCE_TYPES);
    RegistryBuilder<AnimationElement.Type<?>> ANIMATION_ELEMENT_TYPES = makeBuilder(ExtraRegistries.Keys.MODIFIER_TYPES);
    RegistryBuilder<Spawner.Type<?>> SPAWN_ELEMENT_TYPES = makeBuilder(ExtraRegistries.Keys.SPAWNER_TYPES);
    RegistryBuilder<AnimationTerminator.Type<?>> ANIMATION_TERMINATOR_TYPES = makeBuilder(ExtraRegistries.Keys.TERMINATOR_TYPES);
    RegistryBuilder<ParticleFinalizer.Type<?>> PARTICLE_FINALIZER_TYPES = makeBuilder(ExtraRegistries.Keys.FINALIZER_TYPES);
    RegistryBuilder<ActivationTrigger<?>> ACTIVATION_LISTENER_TYPES = makeBuilder(ExtraRegistries.Keys.ACTIVATION_TRIGGERS);
    RegistryBuilder<Modifier.Type<?>> CAMERA_ROTATORS = makeBuilder(ExtraRegistries.Keys.CAMERA_MODIFIERS);
    RegistryBuilder<SpawnEntityFunctionType> SPAWN_FUNCTION_TYPE = makeBuilder(ExtraRegistries.Keys.FUNCTION_TYPES);
    RegistryBuilder<SpawnPoolEntryType> SPAWN_POOL_ENTRY_TYPE = makeBuilder(ExtraRegistries.Keys.POOL_ENTRY_TYPES);
    RegistryBuilder<WearableSlot> WEARABLE_SLOT = makeBuilder(ExtraRegistries.Keys.WEARABLE_SLOTS).add(new ModRegistryCallbacks.WearableSlotsAddCallback()).add(new ModRegistryCallbacks.WearableSlotsCreateCallback());
    RegistryBuilder<InventoryPageType<?>> INVENTORY_PAGE = makeBuilder(ExtraRegistries.Keys.INVENTORY_PAGES);

    private static <T> RegistryBuilder<T> makeBuilder(ResourceKey<Registry<T>> location) {
        RegistryBuilder<T> builder = new RegistryBuilder<T>().setName(location.location());
        builders.add(builder);
        return builder;
    }
}
