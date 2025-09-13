package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.event.custom.RegisterUpdateCheckersEvent;
import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.kapitencraft.kap_lib.item.misc.AnvilUses;
import net.kapitencraft.kap_lib.registry.ExtraRegistryCallbacks;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.util.UpdateChecker;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.ModifyRegistriesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;

@EventBusSubscriber
@ApiStatus.Internal
public class ModEventBusEvents {

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
        ExtraRegistries.registerAll(event::register);
    }

    @SubscribeEvent
    public static void onDataPackRegistryNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ExtraRegistries.Keys.SPAWN_TABLES, SpawnTable.DIRECT_CODEC);
    }


    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(Cooldowns.CAPABILITY, EntityType.PLAYER, (object, context) -> new Cooldowns(object));
        event.registerEntity(Wearables.CAPABILITY, EntityType.PLAYER, (object, context) -> new Wearables(object));
    }

    @SubscribeEvent
    public static void registerUpdateListener(RegisterUpdateCheckersEvent event) {
        event.register(KapLibMod.MOD_ID);
    }
}
