package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;


public interface ExtraRegistryKeys {

    ResourceKey<Registry<GlyphEffect>> GLYPH_EFFECTS = createRegistry("glyph_effects");
    ResourceKey<Registry<IRequestable<?, ?>>> REQUESTABLES = createRegistry("requestables");
    ResourceKey<Registry<DataGenSerializer<? extends ReqCondition<?>>>> REQ_CONDITIONS = createRegistry("requirement_conditions");
    ResourceKey<Registry<DataGenSerializer<? extends Bonus<?>>>> SET_BONUSES = createRegistry("set_bonuses");
    ResourceKey<Registry<Codec<? extends AttributeModifier>>> ATTRIBUTE_MODIFIER_TYPES = vanillaRegistry("attribute_modifier_types");
    ResourceKey<Registry<Codec<? extends ComponentContents>>> COMPONENT_CONTENTS_TYPES = vanillaRegistry("component_contents_types");
    /**
     * used to create codec. very unlikely that anyone finds an actual use for this
     */
    ResourceKey<Registry<Codec<? extends DataSource>>> DATA_SOURCE_TYPES = vanillaRegistry("data_source_types");

    private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
        return ResourceKey.createRegistryKey(KapLibMod.res(id));
    }

    private static <T> ResourceKey<Registry<T>> vanillaRegistry(String id) {
        return ResourceKey.createRegistryKey(new ResourceLocation(id));
    }
}
