package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public interface ExtraRegistries {
    IForgeRegistry<GlyphEffect> GLYPH_EFFECTS = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.GLYPH_EFFECTS);
    IForgeRegistry<IRequestable<?, ?>> REQUESTABLES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.REQUESTABLES);
    IForgeRegistry<DataGenSerializer<? extends ReqCondition<?>>> REQUIREMENT_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.REQ_CONDITIONS);
    IForgeRegistry<DataGenSerializer<? extends Bonus<?>>> BONUS_SERIALIZER = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.SET_BONUSES);
    IForgeRegistry<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.ATTRIBUTE_MODIFIER_TYPES);
    IForgeRegistry<Codec<? extends ComponentContents>> COMPONENT_CONTENT_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.COMPONENT_CONTENTS_TYPES);
    IForgeRegistry<Codec<? extends DataSource>> DATA_SOURCE_TYPES = RegistryManager.ACTIVE.getRegistry(ExtraRegistryKeys.DATA_SOURCE_TYPES);
}
