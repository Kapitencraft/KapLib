package net.kapitencraft.kap_lib.registry.custom.core;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ModRegistryBuilders {

    RegistryBuilder<GlyphEffect> GLYPH_EFFECTS = makeBuilder(ExtraRegistryKeys.GLYPH_EFFECTS);
    RegistryBuilder<IRequestable<?, ?>> REQUESTABLES_BUILDER = makeBuilder(ExtraRegistryKeys.REQUESTABLES);
    RegistryBuilder<DataGenSerializer<? extends ReqCondition<?>>> REQUIREMENTS_BUILDER = makeBuilder(ExtraRegistryKeys.REQ_CONDITIONS);
    RegistryBuilder<DataGenSerializer<? extends Bonus<?>>> SET_BONUSES = makeBuilder(ExtraRegistryKeys.SET_BONUSES);
    RegistryBuilder<Codec<? extends AttributeModifier>> ATTRIBUTE_MODIFIER_TYPES = makeBuilder(ExtraRegistryKeys.ATTRIBUTE_MODIFIER_TYPES);
    RegistryBuilder<Codec<? extends ComponentContents>> COMPONENT_CONTENTS_TYPES = makeBuilder(ExtraRegistryKeys.COMPONENT_CONTENTS_TYPES);
    RegistryBuilder<Codec<? extends DataSource>> DATA_SOURCE_TYPES = makeBuilder(ExtraRegistryKeys.DATA_SOURCE_TYPES);

    private static <T> RegistryBuilder<T> makeBuilder(ResourceKey<Registry<T>> location) {
        return new RegistryBuilder<T>().setName(location.location());
    }
}
