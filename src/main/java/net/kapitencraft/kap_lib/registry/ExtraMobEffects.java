package net.kapitencraft.kap_lib.registry;


import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.mob_effect.StunMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface ExtraMobEffects {
    DeferredRegister<MobEffect> REGISTRY = KapLibMod.registry(Registries.MOB_EFFECT);

    /**
     * stun effect. disables movement
     */
    Holder<MobEffect> STUN = REGISTRY.register("stun", StunMobEffect::new);
}
