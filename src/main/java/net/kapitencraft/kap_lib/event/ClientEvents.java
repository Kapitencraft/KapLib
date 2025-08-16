package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.registry.custom.particle_animation.ActivationTriggers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        ActivationTriggers.ENTITY_ADDED.get().trigger(event.getEntity().getId());
    }
}
