package net.kapitencraft.kap_lib.requirement.event.handler;

import net.kapitencraft.kap_lib.requirement.registry.RequirementRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber
public class RequirementModEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        RequirementRegistries.registerAll(event::register);
    }
}
