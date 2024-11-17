package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.event.custom.RegisterUpdateCheckersEvent;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.registry.custom.core.ModRegistryBuilders;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        ModMessages.register();
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.create(ModRegistryBuilders.REQUESTABLES_BUILDER);
        event.create(ModRegistryBuilders.REQUIREMENTS_BUILDER);
        event.create(ModRegistryBuilders.SET_BONUSES);
        event.create(ModRegistryBuilders.GLYPH_EFFECTS);
    }

    @SubscribeEvent
    public static void registerUpdateListener(RegisterUpdateCheckersEvent event) {
        event.register(KapLibMod.MOD_ID);
    }
}
