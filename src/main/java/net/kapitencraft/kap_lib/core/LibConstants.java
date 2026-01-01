package net.kapitencraft.kap_lib.core;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHooks;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * constants of the library. used to access registries and locations as well as files
 */
@ApiStatus.Internal
public class LibConstants {
    public static final Logger LOGGER = LoggerFactory.getLogger("KapLib");
    public static final String MOD_ID = "kap_lib";

    @ApiStatus.Internal
    public static <T> DeferredRegister<T> registry(ResourceKey<Registry<T>> key) {
        return DeferredRegister.create(key, LibConstants.MOD_ID);
    }

    @ApiStatus.Internal
    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(LibConstants.MOD_ID, path);
    }

    /**
     * root file for any cache data related to KapLib
     * should not be used outside the actual project
     */
    @ApiStatus.Internal
    public static final File ROOT = new File("./kap_lib");

    public static String doubleFormat(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public static boolean gameTestEnabled() {
        return GameTestHooks.isGametestEnabled() && getEnabledNamespaces().contains("kap_lib");
    }

    private static Set<String> getEnabledNamespaces() {
        String enabledNamespacesStr = System.getProperty("neoforge.enabledGameTestNamespaces");
        if (enabledNamespacesStr == null) {
            return Set.of();
        }

        return Arrays.stream(enabledNamespacesStr.split(",")).filter(s -> !s.isBlank()).collect(Collectors.toUnmodifiableSet());
    }

}
