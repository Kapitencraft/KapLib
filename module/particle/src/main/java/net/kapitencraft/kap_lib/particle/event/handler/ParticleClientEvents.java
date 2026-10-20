package net.kapitencraft.kap_lib.particle.event.handler;

import net.kapitencraft.kap_lib.particle.ParticleModule;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.custom.DamageIndicatorParticle;
import net.kapitencraft.kap_lib.particle.custom.LightningParticle;
import net.kapitencraft.kap_lib.particle.custom.ShimmerShieldParticle;
import net.kapitencraft.kap_lib.particle.registry.ExtraParticleTypes;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.ActivationTriggers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ParticleModule.MODULE_ID)
public class ParticleClientEvents {

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ExtraParticleTypes.DAMAGE_INDICATOR.get(), new DamageIndicatorParticle.Provider());
        event.registerSpecial(ExtraParticleTypes.LIGHTNING.get(), new LightningParticle.Provider());
        event.registerSprite(ExtraParticleTypes.SHIMMER_SHIELD.get(), new ShimmerShieldParticle.Provider());
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ClientParticleAnimationManager.INSTANCE);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        ActivationTriggers.ENTITY_ADDED.get().trigger(event.getEntity().getId());
    }
}
