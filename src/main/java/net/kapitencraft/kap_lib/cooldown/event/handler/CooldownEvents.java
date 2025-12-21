package net.kapitencraft.kap_lib.cooldown.event.handler;

import net.kapitencraft.kap_lib.cooldown.CooldownAttributeAdder;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class CooldownEvents {

    @SubscribeEvent
    public static void modifyAttributes(EntityAttributeModificationEvent event) {
        CooldownAttributeAdder.addAttributes(event);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Cooldowns.get(player).tick(player);
    }
}
