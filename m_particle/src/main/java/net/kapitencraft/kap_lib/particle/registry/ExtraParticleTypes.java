package net.kapitencraft.kap_lib.particle.registry;

import net.kapitencraft.kap_lib.core.util.Color;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.custom.DamageIndicatorParticleOptions;
import net.kapitencraft.kap_lib.particle.custom.LightningParticleOptions;
import net.kapitencraft.kap_lib.particle.custom.ShimmerShieldParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;
import java.util.function.Supplier;

@ApiStatus.Internal
public interface ExtraParticleTypes {
    DeferredRegister<ParticleType<?>> REGISTRY = LibConstants.registry(Registries.PARTICLE_TYPE);

    Supplier<DamageIndicatorParticleOptions> DAMAGE_INDICATOR = REGISTRY.register("damage_indicator", () -> new DamageIndicatorParticleOptions(DamageIndicatorParticleOptions.damageIndicatorCoder("heal"), 1, 1));
    Supplier<ShimmerShieldParticleOptions> SHIMMER_SHIELD = REGISTRY.register("shimmer_shield", () -> new ShimmerShieldParticleOptions(0, 0, 0, 0, 0, 0, Color.BLACK_NO_ALPHA, Color.BLACK_NO_ALPHA, 0, UUID.randomUUID()));
    Supplier<LightningParticleOptions> LIGHTNING = REGISTRY.register("lightning", () -> new LightningParticleOptions(Vec3.ZERO, Vec3.ZERO, 2, 100, 0, 0));
}