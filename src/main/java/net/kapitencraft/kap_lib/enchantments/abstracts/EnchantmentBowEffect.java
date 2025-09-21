package net.kapitencraft.kap_lib.enchantments.abstracts;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.registry.ExtraEnchantmentEffectComponents;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface EnchantmentBowEffect {
    Codec<EnchantmentBowEffect> CODEC = ExtraRegistries.ENCHANTMENT_BOW_EFFECTS.byNameCodec().dispatch(EnchantmentBowEffect::codec, Function.identity());

    @ApiStatus.Internal
    Multimap<ResourceLocation, ConditionalEffect<EnchantmentBowEffect>> executionMap = HashMultimap.create();
    LootContextParamSet LOOT_PARAM_SET = LootContextParamSet.builder().required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .optional(LootContextParams.ATTACKING_ENTITY).build();

    @SuppressWarnings("ConstantValue")
    @ApiStatus.Internal
    static float loadFromTag(LivingEntity target, CompoundTag tag, ExePhase type, float oldDamage, AbstractArrow arrow) {
        if (arrow.level() instanceof ServerLevel serverLevel) {
            LootParams.Builder builder = new LootParams.Builder(serverLevel);
            builder.withParameter(LootContextParams.THIS_ENTITY, arrow)
                    .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, target);
            ItemStack weaponItem = arrow.getWeaponItem();
            if (weaponItem != null && !weaponItem.isEmpty()) {
                MutableFloat damage = new MutableFloat(oldDamage);

                EnchantmentHelper.runIterationOnItem(weaponItem, (holder, enchLevel) -> {
                    builder.withParameter(LootContextParams.ENCHANTMENT_LEVEL, enchLevel);
                    List<ConditionalEffect<EnchantmentBowEffect>> effects = holder.value().getEffects(ExtraEnchantmentEffectComponents.BOW.value());
                    ListTag list = tag.getList(holder.getKey().location().toString(), CompoundTag.TAG_COMPOUND);
                    for (int i = 0; i < effects.size(); i++) {
                        ConditionalEffect<EnchantmentBowEffect> effect = effects.get(i);
                        if (effect.matches(new LootContext.Builder(builder.create(EnchantmentBowEffect.LOOT_PARAM_SET)).create(Optional.empty())) && (type != ExePhase.TICK || effect.effect().shouldTick())) {
                            effect.effect().execute(enchLevel, target, list.getCompound(i), type, damage, arrow);
                        }
                    }
                });
                return damage.floatValue();
            }
        }
        return oldDamage;
    }

    /**
     * use to add extra tags which are needed in {@link EnchantmentBowEffect#execute(int, LivingEntity, CompoundTag, ExePhase, MutableFloat, AbstractArrow) execute}, to the bow
     * the enchantment level is written automatically
     */
    void write(CompoundTag tag, int level, ItemStack bow, LivingEntity owner, AbstractArrow arrow);

    /**
     * @param level the enchantment level applied
     * @param target the hit entity, or null if it hit a block, or it's a tick event (see {@code type})
     * @param tag the data saved to the arrow via the {@link EnchantmentBowEffect#write(CompoundTag, int, ItemStack, LivingEntity, AbstractArrow) write} method
     * @param type the type of the execution. either TICK or HIT
     * @param damage the damage the arrow would do (only HIT)
     * @param arrow the arrow that's currently used
     */
    void execute(int level, @Nullable LivingEntity target, CompoundTag tag, ExePhase type, MutableFloat damage, AbstractArrow arrow);

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
