package net.kapitencraft.kap_lib.core.event.handler;

import net.kapitencraft.kap_lib.core.util.DamageCounter;
import net.kapitencraft.kap_lib.core.util.BlockBreakSet;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class CoreEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void utilDamage(LivingDamageEvent.Pre event) {
        DamageCounter.increaseDamage(event.getNewDamage());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void tickVeinMiner(ServerTickEvent.Post event) {
        BlockBreakSet.tickAll();
    }
}
