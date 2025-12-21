package net.kapitencraft.kap_lib.component.registry.custom;

import net.kapitencraft.kap_lib.component.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface ComponentRegistries {

    @ApiStatus.Internal
    List<Registry<?>> registries = new ArrayList<>();

    Registry<GlyphEffect> GLYPH_EFFECTS = syncReg(Keys.GLYPH_EFFECTS);

    private static <T> Registry<T> syncReg(ResourceKey<Registry<T>> key) {
        Registry<T> registry = new RegistryBuilder<>(key).sync(true).create();
        registries.add(registry);
        return registry;
    }

    @ApiStatus.Internal
    static void registerAll(Consumer<Registry<?>> register) {
        registries.forEach(register);
    }

    interface Keys {

        ResourceKey<Registry<GlyphEffect>> GLYPH_EFFECTS = createRegistry("glyph_effects");

        private static <T> ResourceKey<Registry<T>> createRegistry(String id) {
            return ResourceKey.createRegistryKey(LibConstants.res(id));
        }
    }
}
