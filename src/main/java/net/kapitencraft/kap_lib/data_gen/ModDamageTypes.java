package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface ModDamageTypes {
    ResourceKey<DamageType> FEROCITY = register("ferocity");

    static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, KapLibMod.res(name));
    }

    static void bootstrap(BootstapContext<DamageType> damageTypeBootstapContext) {
        damageTypeBootstapContext.register(FEROCITY, new DamageType("ferocity", .1f));
    }
}
