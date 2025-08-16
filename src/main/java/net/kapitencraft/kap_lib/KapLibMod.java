package net.kapitencraft.kap_lib;

import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.config.ServerModConfig;
import net.kapitencraft.kap_lib.crafting.ExtraRecipeTypes;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.kapitencraft.kap_lib.registry.*;
import net.kapitencraft.kap_lib.registry.custom.*;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.*;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnPoolEntries;
import net.kapitencraft.kap_lib.registry.vanilla.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.progress.StartupNotificationManager;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.gametest.GameTestHooks;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.io.File;
import java.text.DecimalFormat;

@Mod(KapLibMod.MOD_ID)
@ApiStatus.Internal
public class KapLibMod {
    public static final String MOD_ID = "kap_lib";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Marker MARKER = Markers.getMarker("KapLib");

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * root file for any cache data related to KapLib
     * should not be used outside the actual project
     */
    @ApiStatus.Internal
    public static final File ROOT = new File("./kap_lib");
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public KapLibMod(IEventBus modEventBus, ModContainer container) {

        ExtraComponentContents.REGISTRY.register(modEventBus);
        ExtraAttributes.REGISTRY.register(modEventBus);
        ExtraLootModifiers.REGISTRY.register(modEventBus);
        ExtraLootItemConditions.REGISTRY.register(modEventBus);
        ExtraParticleTypes.REGISTRY.register(modEventBus);
        ExtraRecipeSerializers.REGISTRY.register(modEventBus);
        ExtraRecipeTypes.REGISTRY.register(modEventBus);
        ExtraMobEffects.REGISTRY.register(modEventBus);

        RequirementTypes.REGISTRY.register(modEventBus);
        BonusTypes.REGISTRY.register(modEventBus);
        GlyphEffects.REGISTRY.register(modEventBus);
        WearableSlots.REGISTRY.register(modEventBus);

        ElementTypes.REGISTRY.register(modEventBus);
        SpawnerTypes.REGISTRY.register(modEventBus);
        FinalizerTypes.REGISTRY.register(modEventBus);
        TerminatorTriggers.REGISTRY.register(modEventBus);
        ActivationTriggers.REGISTRY.register(modEventBus);

        CameraModifiers.REGISTRY.register(modEventBus);

        SpawnEntityFunctions.REGISTRY.register(modEventBus);
        SpawnPoolEntries.REGISTRY.register(modEventBus);

        Overlays.REGISTRY.register(modEventBus);

        AttributeModifierTypes.REGISTRY.register(modEventBus);

        VanillaComponentContentTypes.REGISTRY.register(modEventBus);
        VanillaDataSourceTypes.REGISTRY.register(modEventBus);
        VanillaInventoryPages.REGISTRY.register(modEventBus);

        if (GameTestHooks.isGametestEnabled()) {
            TestItems.REGISTRY.register(modEventBus);
            TestCooldowns.REGISTRY.register(modEventBus);
        }


        container.registerConfig(ModConfig.Type.CLIENT, ClientModConfig.SPEC);
        container.registerConfig(ModConfig.Type.SERVER, ServerModConfig.SPEC);

        NeoForge.EVENT_BUS.addListener(CommandHelper::registerClient);
        NeoForge.EVENT_BUS.addListener(CommandHelper::registerServer);

        ArtifactVersion modVersion = ModList.get().getModContainerById(KapLibMod.MOD_ID).map(ModContainer::getModInfo).map(IModInfo::getVersion).orElse(null);

        if (modVersion == null) throw new IllegalStateException("KapLib version not found");

        StartupNotificationManager.addModMessage("KapLib Mod v" + modVersion + " loaded");
        LOGGER.info(MARKER, "KapLib v{} loaded", modVersion);
    }

    public static String doubleFormat(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public static <T> DeferredRegister<T> registry(ResourceKey<Registry<T>> key) {
        return DeferredRegister.create(key, MOD_ID);
    }
}
