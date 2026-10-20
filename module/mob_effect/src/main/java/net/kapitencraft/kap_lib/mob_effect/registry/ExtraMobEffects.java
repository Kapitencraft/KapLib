package net.kapitencraft.kap_lib.mob_effect.registry;


import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.mob_effect.StunMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface ExtraMobEffects {
    DeferredRegister<MobEffect> REGISTRY = LibConstants.registry(Registries.MOB_EFFECT);

    /**
     * stun effect. disables movement
     */
    Holder<MobEffect> STUN = REGISTRY.register("stun", StunMobEffect::new);
}
