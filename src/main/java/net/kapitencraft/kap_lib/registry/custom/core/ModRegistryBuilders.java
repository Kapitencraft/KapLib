package net.kapitencraft.kap_lib.registry.custom.core;

import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryBuilder;

public interface ModRegistryBuilders {

    RegistryBuilder<GlyphEffect> GLYPH_EFFECTS = makeBuilder(ModRegistryKeys.GLYPH_EFFECTS);
    RegistryBuilder<IRequestable<?, ?>> REQUESTABLES_BUILDER = makeBuilder(ModRegistryKeys.REQUESTABLES);
    RegistryBuilder<DataGenSerializer<? extends ReqCondition<?>>> REQUIREMENTS_BUILDER = makeBuilder(ModRegistryKeys.REQ_CONDITIONS);
    RegistryBuilder<DataGenSerializer<? extends Bonus<?>>> SET_BONUSES = makeBuilder(ModRegistryKeys.SET_BONUSES);

    private static <T> RegistryBuilder<T> makeBuilder(ResourceKey<Registry<T>> location) {
        return new RegistryBuilder<T>().setName(location.location());
    }
}
