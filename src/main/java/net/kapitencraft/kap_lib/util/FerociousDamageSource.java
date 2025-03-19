package net.kapitencraft.kap_lib.util;

import net.kapitencraft.kap_lib.data_gen.ModDamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * damage source for ferocity
 */
public class FerociousDamageSource extends DamageSource {
    public static FerociousDamageSource create(Entity causer, double ferocity, float ferocityDamage) {
        return new FerociousDamageSource(
                causer.level().registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .getOrThrow(ModDamageTypes.FEROCITY),
                causer,
                (float) ferocity,
                ferocityDamage
        );
    }

    public final float ferocity, damage;

    private FerociousDamageSource(Holder<DamageType> pType, @Nullable Entity pEntity, float ferocity, float ferocityDamage) {
        super(pType, pEntity);
        this.ferocity = ferocity;
        this.damage = ferocityDamage;
    }
}
