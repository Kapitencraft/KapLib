package net.kapitencraft.kap_lib.registry.custom.core;

import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.io.network.request.IRequestable;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.requirements.type.abstracts.ReqCondition;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

public interface ModRegistries {
    IForgeRegistry<GlyphEffect> GLYPH_EFFECTS = RegistryManager.ACTIVE.getRegistry(ModRegistryKeys.GLYPH_EFFECTS);
    IForgeRegistry<IRequestable<?, ?>> REQUESTABLES = RegistryManager.ACTIVE.getRegistry(ModRegistryKeys.REQUESTABLES);
    IForgeRegistry<DataGenSerializer<? extends ReqCondition<?>>> REQUIREMENT_TYPE = RegistryManager.ACTIVE.getRegistry(ModRegistryKeys.REQ_CONDITIONS);
    IForgeRegistry<DataGenSerializer<? extends Bonus<?>>> BONUS_SERIALIZER = RegistryManager.ACTIVE.getRegistry(ModRegistryKeys.SET_BONUSES);
}
