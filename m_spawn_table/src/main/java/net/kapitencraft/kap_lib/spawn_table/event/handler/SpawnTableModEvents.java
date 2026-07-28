package net.kapitencraft.kap_lib.spawn_table.event.handler;

import net.kapitencraft.kap_lib.spawn_table.SpawnTable;
import net.kapitencraft.kap_lib.spawn_table.registry.SpawnTableRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public class SpawnTableModEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        SpawnTableRegistries.registerAll(event::register);
    }

    @SubscribeEvent
    public static void onDataPackRegistryNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(SpawnTableRegistries.Keys.SPAWN_TABLES, SpawnTable.DIRECT_CODEC);
    }
}
