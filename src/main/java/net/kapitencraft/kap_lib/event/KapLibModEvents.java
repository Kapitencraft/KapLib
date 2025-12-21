package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.component.registry.custom.ComponentRegistries;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.overlay.registry.custom.OverlayRegistries;
import net.kapitencraft.kap_lib.particle.registry.ParticleAnimationRegistries;
import net.kapitencraft.kap_lib.enchantment.EnchantmentEffectRegistries;
import net.kapitencraft.kap_lib.core.event.custom.RegisterUpdateCheckersEvent;
import net.kapitencraft.kap_lib.bonus.registry.BonusRegistries;
import net.kapitencraft.kap_lib.item.creative_tab.TabGroup;
import net.kapitencraft.kap_lib.item.misc.AnvilUses;
import net.kapitencraft.kap_lib.camera.registry.custom.CameraRegistries;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.core.util.UpdateChecker;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;

@EventBusSubscriber
@ApiStatus.Internal
public class KapLibModEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        AnvilUses.registerUses();
    }

    @SubscribeEvent
    public static void containerLoadEvent(FMLConstructModEvent event) {
        UpdateChecker.run();
    }

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        ParticleAnimationRegistries.registerAll(event::register);
        EnchantmentEffectRegistries.registerAll(event::register);
        BonusRegistries.registerAll(event::register);
        SpawnTableRegistries.registerAll(event::register);
        ComponentRegistries.registerAll(event::register);
        CameraRegistries.registerAll(event::register);
        OverlayRegistries.registerAll(event::register);
    }

    @SubscribeEvent
    public static void onDataPackRegistryNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(SpawnTableRegistries.Keys.SPAWN_TABLES, SpawnTable.DIRECT_CODEC);
    }

    @SubscribeEvent
    public static void registerUpdateListener(RegisterUpdateCheckersEvent event) {
        event.register(LibConstants.MOD_ID);
    }

    @SubscribeEvent
    public static void addToTabs(BuildCreativeModeTabContentsEvent event) {
        TabGroup.registerAll(event);
    }
}
