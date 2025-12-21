package net.kapitencraft.kap_lib.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;

@ApiStatus.Internal
public class LibConstants {
    public static final String MOD_ID = "kap_lib";

    public static <T> DeferredRegister<T> registry(ResourceKey<Registry<T>> key) {
        return DeferredRegister.create(key, LibConstants.MOD_ID);
    }

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(LibConstants.MOD_ID, path);
    }

    /**
     * root file for any cache data related to KapLib
     * should not be used outside the actual project
     */
    @ApiStatus.Internal
    public static final File ROOT = new File("./kap_lib");

}
