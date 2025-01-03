package net.kapitencraft.kap_lib.client.particle.animation;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * manager of all active animations
 */
public final class ParticleAnimationManager {
    List<ParticleAnimator> activeAnimations = new ArrayList<>();

    /**
     * use {@link ParticleAnimation.Builder#register()}<br>
     * or {@link ParticleAnimation.Builder#sendToPlayer(ServerPlayer)}<br>
     * or {@link  ParticleAnimation.Builder#sendToAllPlayers(ServerLevel)} to add animations
     */
    @ApiStatus.Internal
    public void accept(ParticleAnimation animation) {
        activeAnimations.add(new ParticleAnimator(animation));
    }

    @ApiStatus.Internal
    public void tick(RandomSource source) {
        activeAnimations.forEach(pa -> pa.tick(source));
        activeAnimations.removeIf(ParticleAnimator::beenTerminated);
    }
}
