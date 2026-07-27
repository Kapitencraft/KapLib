package net.kapitencraft.kap_lib.enchantment.abstracts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.enchantment.EnchantmentEffectRegistries;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.kapitencraft.kap_lib.core.io.serialization.NbtSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public interface EnchantmentCountEffect {
    Codec<EnchantmentCountEffect> CODEC = EnchantmentEffectRegistries.COUNT.byNameCodec().dispatch(EnchantmentCountEffect::codec, Function.identity());

    Codec<Map<UUID, Integer>> DATA_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Codec.INT);
    NbtSerializer<Map<UUID, Integer>> SERIALIZER = new NbtSerializer<>(DATA_CODEC, HashMap::new);

    @ApiStatus.Internal
    default String mapName(Holder<Enchantment> holder) {
        return Objects.requireNonNull(holder.getKey(), "unknown enchantment").location().toString();
    }

    CountType countType();

    int getCountAmount(int level);

    default float execute(Holder<Enchantment> holder, int level, EnchantedItemInUse item, LivingEntity attacker, LivingEntity attacked, float damageAmount, DamageSource source, float attackStrenghtScale) {
        CompoundTag attackerTag = IOHelper.getOrCreateCompound(attacker.getPersistentData(), "CountEnchantment");
        String mapName = this.mapName(holder);
        HashMap<UUID, Integer> map = new HashMap<>(SERIALIZER.parse(attackerTag.contains(mapName, 10) ? attackerTag.get(mapName) : new CompoundTag()));
        map.putIfAbsent(attacked.getUUID(), 1);
        int i = map.get(attacked.getUUID());
        if (i >= this.getCountAmount(level)) {
            if (this.countType() != CountType.EXCEPT) {
                damageAmount = this.mainExecute(level, item.itemStack(), attacker, attacked, damageAmount, 0, source, attackStrenghtScale);
            }
            i = this.countType() == CountType.ONCE ? -1 : 1;
        } else {
            if (i >= 0) {
                if (this.countType() != CountType.NORMAL) damageAmount = this.mainExecute(level, item.itemStack(), attacker, attacked, damageAmount, i, source, attackStrenghtScale);
                i++;
            }
        }
        map.put(attacked.getUUID(), i);
        attackerTag.put(mapName, SERIALIZER.encode(map));
        return damageAmount;
    }

    default float tryExecute(Holder<Enchantment> holder, int level, EnchantedItemInUse enchanted, LivingEntity attacker, LivingEntity attacked, float damage, DamageSource source) {
        float attackStrengthScale = 1;
        if (attacker instanceof Player p) {
            attackStrengthScale = p.getAttackStrengthScale(0);
        }
        return this.execute(holder, level, enchanted, attacker, attacked, damage, source, attackStrengthScale);
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
        ONCE
    }

    MapCodec<? extends EnchantmentCountEffect> codec();
}
