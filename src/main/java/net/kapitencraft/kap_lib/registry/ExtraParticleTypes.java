package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.DamageIndicatorParticleOptions;
import net.kapitencraft.kap_lib.client.particle.LightningParticleOptions;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticleOptions;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;
import java.util.function.Supplier;

@ApiStatus.Internal
public interface ExtraParticleTypes {
    DeferredRegister<ParticleType<?>> REGISTRY = KapLibMod.registry(Registries.PARTICLE_TYPE);

    Supplier<DamageIndicatorParticleOptions> DAMAGE_INDICATOR = REGISTRY.register("damage_indicator", () -> new DamageIndicatorParticleOptions(TextHelper.damageIndicatorCoder("heal"), 1, 1));
    Supplier<ShimmerShieldParticleOptions> SHIMMER_SHIELD = REGISTRY.register("shimmer_shield", ()-> new ShimmerShieldParticleOptions(0, 0, 0, 0, 0, 0, new Color(0), new Color(0), 0, UUID.randomUUID()));
    Supplier<LightningParticleOptions> LIGHTNING = REGISTRY.register("lightning", () -> new LightningParticleOptions(Vec3.ZERO, Vec3.ZERO, 2, 100, 0, 0));
}