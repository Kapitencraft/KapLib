package net.kapitencraft.kap_lib.attribute.damage;

import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * damage source for ferocity
 */
@ApiStatus.Internal
public class FerociousDamageSource extends DamageSource {
    public static FerociousDamageSource create(Entity causer, double ferocity, float ferocityDamage) {
        return new FerociousDamageSource(
                MiscHelper.lookupDamageTypeHolder(causer.level(), AttributeDamageTypes.FEROCITY),
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
