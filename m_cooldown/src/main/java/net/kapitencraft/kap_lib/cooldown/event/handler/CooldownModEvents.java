package net.kapitencraft.kap_lib.cooldown.event.handler;

import net.kapitencraft.kap_lib.cooldown.registry.CooldownRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;


@EventBusSubscriber
@ApiStatus.Internal
public class CooldownModEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        CooldownRegistries.registerAll(event::register);
    }
}
