package net.kapitencraft.kap_lib.bonus.event.handler;

import net.kapitencraft.kap_lib.bonus.registry.BonusRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;


@EventBusSubscriber
@ApiStatus.Internal
public class ModEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        BonusRegistries.registerAll(event::register);
    }
}
