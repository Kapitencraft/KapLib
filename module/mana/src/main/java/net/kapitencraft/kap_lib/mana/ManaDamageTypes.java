package net.kapitencraft.kap_lib.mana;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface ManaDamageTypes {
    ResourceKey<DamageType> MANA_OVERFLOW = register("mana_overflow");
    ResourceKey<DamageType> MANA_OVERFLOW_SELF = register("mana_overflow_self");

    static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, LibConstants.res(name));
    }

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(MANA_OVERFLOW, new DamageType("mana_overflow", 2f));
        context.register(MANA_OVERFLOW_SELF, new DamageType("mana_overflow_self", 20f));
    }
}
