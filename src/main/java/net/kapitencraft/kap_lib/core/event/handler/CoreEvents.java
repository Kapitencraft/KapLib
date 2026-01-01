package net.kapitencraft.kap_lib.core.event.handler;

import net.kapitencraft.kap_lib.core.util.DamageCounter;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber
public class CoreEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void utilDamage(LivingDamageEvent.Pre event) {
        DamageCounter.increaseDamage(event.getNewDamage());
    }
}
