package net.kapitencraft.kap_lib.cooldown.event.handler;

import net.kapitencraft.kap_lib.cooldown.CooldownAttributeAdder;
import net.kapitencraft.kap_lib.cooldown.CooldownModule;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownRegistries;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = CooldownModule.MODULE_ID)
public class CooldownEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Cooldowns.get(player).tick(player);
    }

    @SubscribeEvent
    public static void addRegistries(NewRegistryEvent event) {
        CooldownRegistries.registerAll(event::register);
    }

    @SubscribeEvent
    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        CooldownAttributeAdder.addAttributes(event);
    }
}
