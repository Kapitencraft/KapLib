package net.kapitencraft.kap_lib.two_handed.mixin.duck;

public interface OffhandAttackCooldownHolder {

    float getOffhandAttackStrengthScale(float adjustTicks);

    void resetOffhandAttackStrengthTicker();
}