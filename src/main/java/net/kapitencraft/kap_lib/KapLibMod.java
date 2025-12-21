package net.kapitencraft.kap_lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.kapitencraft.kap_lib.bonus.registry.BonusTypes;
import net.kapitencraft.kap_lib.camera.CameraClientTestCommand;
import net.kapitencraft.kap_lib.commands.ClientTestCommand;
import net.kapitencraft.kap_lib.commands.ComponentClientTestCommand;
import net.kapitencraft.kap_lib.commands.ServerTestCommand;
import net.kapitencraft.kap_lib.core.config.CoreClientModConfig;
import net.kapitencraft.kap_lib.core.config.ServerModConfig;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttachmentTypes;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.Markers;
import net.kapitencraft.kap_lib.enchantment.ExtraEnchantmentEffectComponents;
import net.kapitencraft.kap_lib.enchantment.client.enchantment_color.ConfigureEnchantmentColorsCommand;
import net.kapitencraft.kap_lib.inventory_page.registry.VanillaInventoryPages;
import net.kapitencraft.kap_lib.inventory_page.registry.WearableSlots;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootItemConditions;
import net.kapitencraft.kap_lib.loot.registry.ExtraLootModifiers;
import net.kapitencraft.kap_lib.mana.ManaAttachmentTypes;
import net.kapitencraft.kap_lib.mana.ManaAttributes;
import net.kapitencraft.kap_lib.mana.advancement.ExtraCriterionTriggers;
import net.kapitencraft.kap_lib.overlay.OverlaysCommand;
import net.kapitencraft.kap_lib.overlay.registry.Overlays;
import net.kapitencraft.kap_lib.particle.ParticleClientTestCommand;
import net.kapitencraft.kap_lib.particle.registry.ExtraParticleTypes;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.*;
import net.kapitencraft.kap_lib.recipe.registry.ExtraRecipeSerializers;
import net.kapitencraft.kap_lib.recipe.registry.ExtraRecipeTypes;
import net.kapitencraft.kap_lib.mob_effect.registry.ExtraMobEffects;
import net.kapitencraft.kap_lib.registry.TestCooldowns;
import net.kapitencraft.kap_lib.registry.TestItems;
import net.kapitencraft.kap_lib.camera.registry.CameraModifiers;
import net.kapitencraft.kap_lib.component.registry.GlyphEffects;
import net.kapitencraft.kap_lib.requirement.registry.RequirementTypes;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.registry.spawn_table.SpawnPoolEntries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.progress.StartupNotificationManager;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.gametest.GameTestHooks;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.text.DecimalFormat;

@Mod(LibConstants.MOD_ID)
@ApiStatus.Internal
public class KapLibMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Marker MARKER = Markers.getMarker("KapLib");

    public static ResourceLocation res(String path) {
        return ResourceLocation.fromNamespaceAndPath(LibConstants.MOD_ID, path);
    }

    public KapLibMod(IEventBus modEventBus, ModContainer container) {

        ExtraAttributes.REGISTRY.register(modEventBus);
        ManaAttributes.REGISTRY.register(modEventBus);
        ExtraLootModifiers.REGISTRY.register(modEventBus);
        ExtraLootItemConditions.REGISTRY.register(modEventBus);
        ExtraParticleTypes.REGISTRY.register(modEventBus);
        ExtraRecipeSerializers.REGISTRY.register(modEventBus);
        ExtraRecipeTypes.REGISTRY.register(modEventBus);
        ExtraMobEffects.REGISTRY.register(modEventBus);
        ExtraEnchantmentEffectComponents.REGISTRY.register(modEventBus);
        ExtraCriterionTriggers.REGISTRY.register(modEventBus);

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

        CooldownAttachmentTypes.REGISTRY.register(modEventBus);
        ManaAttachmentTypes.REGISTRY.register(modEventBus);

        VanillaInventoryPages.REGISTRY.register(modEventBus);

        if (GameTestHooks.isGametestEnabled()) {
            TestItems.REGISTRY.register(modEventBus);
            TestCooldowns.REGISTRY.register(modEventBus);
        }

        container.registerConfig(ModConfig.Type.CLIENT, CoreClientModConfig.SPEC);
        container.registerConfig(ModConfig.Type.SERVER, ServerModConfig.SPEC);

        NeoForge.EVENT_BUS.addListener(KapLibMod::registerClient);
        NeoForge.EVENT_BUS.addListener(KapLibMod::registerServer);

        NeoForgeMod.enableMergedAttributeTooltips();

        ArtifactVersion modVersion = ModList.get().getModContainerById(LibConstants.MOD_ID).map(ModContainer::getModInfo).map(IModInfo::getVersion).orElse(null);

        if (modVersion == null) throw new IllegalStateException("KapLib version not found");

        StartupNotificationManager.addModMessage("KapLib Mod v" + modVersion + " loaded");
        LOGGER.info(MARKER, "KapLib v{} loaded", modVersion);
    }

    public static String doubleFormat(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    /**
     * register library commands
     */
    @ApiStatus.Internal
    static void registerClient(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        OverlaysCommand.register(dispatcher);
        ClientTestCommand.register(dispatcher);
        ParticleClientTestCommand.register(dispatcher);
        CameraClientTestCommand.register(dispatcher);
        ComponentClientTestCommand.register(dispatcher);
        ConfigureEnchantmentColorsCommand.register(dispatcher);
    }

    @ApiStatus.Internal
    static void registerServer(RegisterCommandsEvent event) {
        ServerTestCommand.register(event.getDispatcher());
    }

}
