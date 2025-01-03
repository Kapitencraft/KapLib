package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.particle.DamageIndicatorParticleOptions;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticleOptions;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public interface ExtraParticleTypes {
    DeferredRegister<ParticleType<?>> REGISTRY = KapLibMod.registry(ForgeRegistries.PARTICLE_TYPES);

    RegistryObject<DamageIndicatorParticleOptions> DAMAGE_INDICATOR = REGISTRY.register("damage_indicator", () -> new DamageIndicatorParticleOptions(TextHelper.damageIndicatorCoder("heal"), 1, 1));
    RegistryObject<ShimmerShieldParticleOptions> SHIMMER_SHIELD = REGISTRY.register("shimmer_shield", ()-> new ShimmerShieldParticleOptions(0, 0, 0, 0, 0, 0, new Color(0), new Color(0), 0, UUID.randomUUID()));
}