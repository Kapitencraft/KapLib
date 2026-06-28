package net.kapitencraft.kap_lib.two_handed.mixin.duck;

public interface OffhandAttackCooldownHolder {

    float getOffhandAttackStrengthScale(float adjustTicks);

    void resetOffhandAttackStrengthTicker();

    boolean shouldAttackOffhand();

    float getUsedAttackStrengthScale(float adjustTicks);

    void swapToOffhandAttributes();

    void swapToMainhandAttributes();
}