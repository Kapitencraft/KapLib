package net.kapitencraft.kap_lib.bonus;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.bonus.registry.BonusRegistries;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface BonusEffect {
    Codec<BonusEffect> CODEC = BonusRegistries.EFFECT_SERIALIZERS.byNameCodec().dispatchStable(BonusEffect::getSerializer, RegistrySerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, BonusEffect> STREAM_CODEC = ByteBufCodecs.registry(BonusRegistries.Keys.EFFECT_SERIALIZERS).dispatch(BonusEffect::getSerializer, RegistrySerializer::streamCodec);

    /**
     * called whenever a LivingEntity equips an item with this bonus
     *
     * @param living the entity this bonus applied to
     */
    default void onApply(LivingEntity living) {
    }

    /**
     * called whenever an entity un-equips this bonus. <br>
     * this will also be called when the bonus was never added due to being disabled
     * via {@link AbstractBonusElement#isActive(LivingEntity)}
     *
     * @param living the entity this bonus was previously applied to
     */
    default void onRemove(LivingEntity living) {
    }

    @Nullable
    default Cooldown getCooldown() {
        return null;
    }

    /**
     * @return the serializer for this bonus type.
     * <br>must be registered to the {@link BonusRegistries#EFFECT_SERIALIZERS} registry
     */
    RegistrySerializer<? extends BonusEffect> getSerializer();

    /**
     * @param tickCount the count of ticks since this bonus has been activated
     * @param living    the entity the bonus is applied to
     * @return if this tick should apply a tick (similar to how the actual {@link net.minecraft.world.effect.MobEffect MobEffect} works)
     */
    default boolean isEffectTick(int tickCount, LivingEntity living) {
        return false;
    }

    /**
     * applied each tick that {@link BonusEffect#isEffectTick(int, LivingEntity)} returns true
     * only called serverside
     *
     * @param tickCount count of ticks since this bonus has been activated
     * @param entity    the entity the bonus is applied to
     */
    default void onTick(int tickCount, @NotNull LivingEntity entity) {
    }

    /**
     * called when the holder of this bonus kills another entity.
     *
     * @param killed the entity that has been killed
     * @param holder the holder of this bonus that killed the target
     * @param source the damage source that was used to kill this entity
     */
    default void onEntityKilled(LivingEntity killed, LivingEntity holder, DamageSource source) {
    }

    /**
     * provides modifiers this bonus should add to the entity it is hold by.
     *
     * @param living the entity applied to
     * @return all attribute modifiers this bonus should apply to the given entity. should always return an equal value
     */
    default @Nullable Multimap<Holder<Attribute>, AttributeModifier> getModifiers(LivingEntity living) {
        return null;
    }

    /**
     * called when the holder of this bonus attacks another entity
     *
     * @param attacked  the attack target
     * @param attacker  the holder of this bonus and attacker in this attack
     * @param container damage container handling modification
     */
    default void onEntityHurt(LivingEntity attacked, LivingEntity attacker, DamageContainer container) {
    }


    /**
     * called when an entity attacks the holder of this owner
     *
     * @param attacked  the holder of this bonus and target of the attack
     * @param attacker  the attacker
     * @param container damage container handling modification
     */
    default void onHolderTakeDamage(LivingEntity attacked, LivingEntity attacker, DamageContainer container) {
    }
}