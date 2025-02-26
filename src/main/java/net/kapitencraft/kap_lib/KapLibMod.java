package net.kapitencraft.kap_lib;

import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.config.ServerModConfig;
import net.kapitencraft.kap_lib.crafting.ExtraRecipeTypes;
import net.kapitencraft.kap_lib.helpers.CommandHelper;
import net.kapitencraft.kap_lib.registry.*;
import net.kapitencraft.kap_lib.registry.custom.SetBonusTypes;
import net.kapitencraft.kap_lib.registry.custom.RequirementTypes;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.*;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaAttributeModifierTypes;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaComponentContentTypes;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaDataSourceTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
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

    public static ResourceLocation res(String sub) {
        return new ResourceLocation(MOD_ID, sub);
    }

    public static final File MAIN = new File("./kap_lib");
    public static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public KapLibMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ExtraAttributes.REGISTRY.register(modEventBus);
        ExtraLootModifiers.REGISTRY.register(modEventBus);
        ExtraLootItemConditions.REGISTRY.register(modEventBus);
        ExtraParticleTypes.REGISTRY.register(modEventBus);
        ExtraRecipeSerializers.REGISTRY.register(modEventBus);
        ExtraRecipeTypes.REGISTRY.register(modEventBus);
        ExtraMobEffects.REGISTRY.register(modEventBus);

        RequirementTypes.REGISTRY.register(modEventBus);
        SetBonusTypes.REGISTRY.register(modEventBus);
        GlyphEffects.REGISTRY.register(modEventBus);

        ElementTypes.REGISTRY.register(modEventBus);
        SpawnerTypes.REGISTRY.register(modEventBus);
        FinalizerTypes.REGISTRY.register(modEventBus);
        TerminatorTypes.REGISTRY.register(modEventBus);
        ActivationTriggers.REGISTRY.register(modEventBus);
        Overlays.REGISTRY.register(modEventBus);

        VanillaAttributeModifierTypes.REGISTRY.register(modEventBus);
        VanillaComponentContentTypes.REGISTRY.register(modEventBus);
        VanillaDataSourceTypes.REGISTRY.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientModConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerModConfig.SPEC);

        MinecraftForge.EVENT_BUS.addListener(CommandHelper::registerClient);
        MinecraftForge.EVENT_BUS.addListener(CommandHelper::registerServer);

        ArtifactVersion modVersion = ModList.get().getModContainerById(KapLibMod.MOD_ID).map(ModContainer::getModInfo).map(IModInfo::getVersion).orElse(null);

        if (modVersion == null) throw new IllegalStateException("kap lib version not found");

        StartupMessageManager.addModMessage("KapLib Mod v" + modVersion + " loaded");
        LOGGER.info(MARKER, "KapLib v{} loaded", modVersion);
    }

    public static String doubleFormat(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public static <T> DeferredRegister<T> registry(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, MOD_ID);
    }

    public static <T> DeferredRegister<T> registry(ResourceKey<Registry<T>> key) {
        return DeferredRegister.create(key, MOD_ID);
    }
}
