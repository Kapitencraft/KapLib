package net.kapitencraft.kap_lib.particle.event.handler;

import net.kapitencraft.kap_lib.particle.custom.DamageIndicatorParticleOptions;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

@EventBusSubscriber
public class ParticleEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void healingDisplay(LivingHealEvent event) {
        if (event.getAmount() > 0) DamageIndicatorParticleOptions.create(event.getEntity(), event.getAmount(), "heal");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void utilDamage(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        DamageIndicatorParticleOptions.create(attacked, event.getNewDamage(), source.getMsgId());
    }

    @SubscribeEvent
    public static void leaveLevelEvent(EntityLeaveLevelEvent event) {
        if (event.getEntity().level().isClientSide()) {
            TerminatorTriggers.ENTITY_REMOVED.get().trigger(event.getEntity().getId());
        }
    }
}
