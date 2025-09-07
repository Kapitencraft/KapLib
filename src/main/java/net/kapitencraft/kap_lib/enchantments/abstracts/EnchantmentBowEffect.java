package net.kapitencraft.kap_lib.enchantments.abstracts;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface EnchantmentBowEffect {
    Codec<EnchantmentBowEffect> CODEC = ExtraRegistries.ENCHANTMENT_BOW_EFFECT_TYPE.byNameCodec().dispatch(EnchantmentBowEffect::codec, Function.identity());

    @ApiStatus.Internal
    Multimap<ResourceLocation, Execution> executionMap = HashMultimap.create();

    static int getLevel(CompoundTag tag) {
        return tag.getInt("Level");
    }

    interface Execution {
        float execute(int enchantLevel, LivingEntity target, CompoundTag tag, ExePhase type, float oldDamage, AbstractArrow arrow);
    }

    @ApiStatus.Internal
    static float loadFromTag(LivingEntity target, CompoundTag tag, ExePhase type, float oldDamage, AbstractArrow arrow) {
        for (ResourceLocation location : executionMap.keySet()) {
            String string = location.toString();
            if (tag.contains(string, 10)) {
                CompoundTag elementTag = tag.getCompound(string);
                int level = getLevel(elementTag);
                for (Execution execution : executionMap.get(location)) {
                    oldDamage = execution.execute(level, target, elementTag, type, oldDamage, arrow);
                }
            }
        }
        return oldDamage;
    }

    /**
     * use to add extra tags which are needed in {@link EnchantmentBowEffect#execute(int, LivingEntity, CompoundTag, ExePhase, float, AbstractArrow) execute}, to the bow
     * the enchantment level is written automatically
     * @return the populated data
     */
    CompoundTag write(CompoundTag tag, int level, ItemStack bow, LivingEntity owner, AbstractArrow arrow);

    /**
     * @param level the enchantment level applied
     * @param target the hit entity, or null if it hit a block, or it's a tick event (see {@code type})
     * @param tag the data saved to the arrow via the {@link EnchantmentBowEffect#write(CompoundTag, int, ItemStack, LivingEntity, AbstractArrow) write} method
     * @param type the type of the execution. either TICK or HIT
     * @param oldDamage the damage the arrow would do (only HIT)
     * @param arrow the arrow that's currently used
     * @return the new damage value (ignored in TICK phase)
     */
    float execute(int level, @Nullable LivingEntity target, CompoundTag tag, ExePhase type, float oldDamage, AbstractArrow arrow);

    /**
     * determines whether the enchantment should tick on arrows
     */
    boolean shouldTick();

    enum ExePhase {
        /**
         * the TICK execution type. only used when the Enchantment specifies {@link EnchantmentBowEffect#shouldTick()} as true
         */
        TICK,
        /**
         * the HIT execution type. executed when the Arrow hits a block / entity
         */
        HIT;
    }

    MapCodec<? extends EnchantmentBowEffect> codec();
}
