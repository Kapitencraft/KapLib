package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface ModDamageTypes {
    ResourceKey<DamageType> FEROCITY = register("ferocity");
    ResourceKey<DamageType> MANA_OVERFLOW = register("mana_overflow");
    ResourceKey<DamageType> MANA_OVERFLOW_SELF = register("mana_overflow_self");

    static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, KapLibMod.res(name));
    }

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(FEROCITY, new DamageType("ferocity", .1f));
        context.register(MANA_OVERFLOW, new DamageType("mana_overflow", 2f));
        context.register(MANA_OVERFLOW_SELF, new DamageType("mana_overflow_self", 20f));
    }
}
