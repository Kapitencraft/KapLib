package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.client.LibClient;
import net.kapitencraft.kap_lib.client.particle.animation.activation_triggers.core.TriggerInstance;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.ActivationTriggers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        ActivationTriggers.ENTITY_ADDED.get().trigger(event.getEntity().getId());
    }
}
