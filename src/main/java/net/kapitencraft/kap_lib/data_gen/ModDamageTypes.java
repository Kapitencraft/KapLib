package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.attribute.damage.AttributeDamageTypes;
import net.kapitencraft.kap_lib.mana.ManaDamageTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

public interface ModDamageTypes {

    static void bootstrap(BootstrapContext<DamageType> context) {
        AttributeDamageTypes.bootstrap(context);
        ManaDamageTypes.bootstrap(context);
    }
}
