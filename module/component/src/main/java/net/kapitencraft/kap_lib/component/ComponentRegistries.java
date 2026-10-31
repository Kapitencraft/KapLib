package net.kapitencraft.kap_lib.component;

import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public interface ComponentRegistries {

    Registry<GlyphEffect<?>> GLYPH_EFFECTS = syncReg(Keys.GLYPH_EFFECTS);

    @SuppressWarnings("SameParameterValue")
    private static <T> Registry<T> syncReg(ResourceKey<Registry<T>> key) {
        return new RegistryBuilder<>(key).sync(true).create();
    }

    interface Keys {
        ResourceKey<Registry<GlyphEffect<?>>> GLYPH_EFFECTS = createRegistry("bonus_serializers");

        @SuppressWarnings("SameParameterValue")
        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }
}
