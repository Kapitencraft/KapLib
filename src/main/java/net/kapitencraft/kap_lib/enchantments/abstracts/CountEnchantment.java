package net.kapitencraft.kap_lib.enchantments.abstracts;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.kapitencraft.kap_lib.io.serialization.NbtSerializer;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public interface CountEnchantment extends ExtendedCalculationEnchantment, IWeaponEnchantment {
    Codec<Map<UUID, Integer>> DATA_CODEC = Codec.unboundedMap(ExtraCodecs.UUID, Codec.INT);
    NbtSerializer<Map<UUID, Integer>> SERIALIZER = new NbtSerializer<>(DATA_CODEC, HashMap::new);

    @ApiStatus.Internal
    default String mapName() {
        return Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey((Enchantment) this), "unknown enchantment").toString();
    }

    CountType countType();

    int getCountAmount(int level);

    @Override
    default float execute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, float damageAmount, DamageSource source, float attackStrenghtScale) {
        CompoundTag attackerTag = IOHelper.getOrCreateTag(attacker.getPersistentData(), "CountEnchantment");
        String mapName = this.mapName();
        HashMap<UUID, Integer> map = new HashMap<>(SERIALIZER.parse(attackerTag.contains(mapName, 10) ? attackerTag.get(mapName) : new CompoundTag()));
        map.putIfAbsent(attacked.getUUID(), 1);
        int i = map.get(attacked.getUUID());
        if (i >= this.getCountAmount(level)) {
            if (this.countType() != CountType.EXCEPT) {
                damageAmount = this.mainExecute(level, enchanted, attacker, attacked, damageAmount, 0, source, attackStrenghtScale);
            }
            i = this.countType() == CountType.ONCE ? -1 : 1;
        } else {
            if (i >= 0) {
                if (this.countType() != CountType.NORMAL) damageAmount = this.mainExecute(level, enchanted, attacker, attacked, damageAmount, i, source, attackStrenghtScale);
                i++;
            }
        }
        map.put(attacked.getUUID(), i);
        attackerTag.put(mapName, SERIALIZER.encode(map));
        return damageAmount;
    }

    float mainExecute(int level, ItemStack enchanted, LivingEntity attacker, LivingEntity attacked, float damageAmount, int curHit, DamageSource source, float attackStrenghtScale);

    enum CountType {
        /**
         * executed when reaching the given count
         */
        NORMAL,
        /**
         * executed except the counter is the given count
         */
        EXCEPT,
        /**
         * executed counter times, and then never again
         */
        ONCE;
    }
}
