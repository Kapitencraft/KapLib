package net.kapitencraft.kap_lib.attribute.damage;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public interface AttributeDamageTypes {
    ResourceKey<DamageType> FEROCITY = register("ferocity");

    static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, LibConstants.res(name));
    }

    static void bootstrap(BootstrapContext<DamageType> context) {
        context.register(FEROCITY, new DamageType("ferocity", .1f));
    }
}
