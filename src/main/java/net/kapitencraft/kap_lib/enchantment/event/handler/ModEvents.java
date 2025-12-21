package net.kapitencraft.kap_lib.enchantment.event.handler;

import net.kapitencraft.kap_lib.enchantment.EnchantmentEffectRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.jetbrains.annotations.ApiStatus;


@EventBusSubscriber
@ApiStatus.Internal
public class ModEvents {

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        EnchantmentEffectRegistries.registerAll(event::register);
    }
}
