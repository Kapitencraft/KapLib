package net.kapitencraft.kap_lib.registry;


import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.mob_effect.StunMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ExtraMobEffects {
    DeferredRegister<MobEffect> REGISTRY = KapLibMod.registry(ForgeRegistries.MOB_EFFECTS);

    RegistryObject<StunMobEffect> STUN = REGISTRY.register("stun", StunMobEffect::new);
}
