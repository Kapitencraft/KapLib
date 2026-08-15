package net.kapitencraft.kap_lib.attribute.compat;

import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.elements.MoveTowardsBBElement;
import net.kapitencraft.kap_lib.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.particle.animation.spawners.EntityBBSpawner;
import net.kapitencraft.kap_lib.particle.animation.terminators.EntityRemovedTerminatorTrigger;
import net.kapitencraft.kap_lib.particle.animation.terminators.TimedTerminator;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

public class ParticleCompat {

    public static void sendLifeStealAnimation(LivingEntity attacked, @NotNull LivingEntity attacker) {
        ParticleAnimation.builder()
                .spawn(EntityBBSpawner.builder()
                        .setTexture(new DustParticleOptions(Vec3.fromRGB24(0x800000).toVector3f(), .3f))
                        .target(attacked)
                        .perTick(150)
                        .scaleX(1.3f).scaleY(1.1f)
                ).then(MoveTowardsBBElement.builder()
                        .target(attacker)
                        .duration(30)
                ).finalizes(RemoveParticleFinalizer.builder())
                .spawnTime(ParticleAnimation.SpawnTime.once())
                .terminatedWhen(TimedTerminator.ticks(20))
                .terminatedWhen(EntityRemovedTerminatorTrigger.create(attacked))
                .terminatedWhen(EntityRemovedTerminatorTrigger.create(attacker))
                .sendToAllPlayers(SendLifeStealAnimationPacket::new);
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(SendLifeStealAnimationPacket.TYPE, SendLifeStealAnimationPacket.STREAM_CODEC, SendLifeStealAnimationPacket::handle);
    }
}
