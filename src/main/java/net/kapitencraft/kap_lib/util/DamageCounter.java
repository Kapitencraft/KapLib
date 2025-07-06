package net.kapitencraft.kap_lib.util;

public class DamageCounter {
    private static float damage = 0;
    private static int attacked = 0;
    private static boolean shouldCount = false;

    public static void increaseDamage(float damageToAdd) {
        if (shouldCount) {
            damage += damageToAdd;
            attacked++;
        }
    }

    public static void activate() {
        shouldCount = true;
    }

    public static DamageHolder getDamage(boolean reset) {
        DamageHolder holder = new DamageHolder(attacked, damage);
        if (reset) {
            attacked = 0;
                damage = 0;
            shouldCount = false;
        }
        return holder;
    }

    public record DamageHolder(int hit, float damage) {

        public boolean hasDamage() {
            return hit > 0 && damage > 0;
        }
    }
}
