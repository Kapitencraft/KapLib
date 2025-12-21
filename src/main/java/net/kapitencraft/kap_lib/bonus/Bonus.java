package net.kapitencraft.kap_lib.bonus;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.bonus.registry.BonusRegistries;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface Bonus<T extends Bonus<T>> extends IEventListener {
    Codec<Bonus<?>> CODEC = BonusRegistries.SERIALIZERS.byNameCodec().dispatchStable(Bonus::getSerializer, RegistrySerializer::codec);
    StreamCodec<RegistryFriendlyByteBuf, Bonus<?>> STREAM_CODEC = ByteBufCodecs.registry(BonusRegistries.Keys.SERIALIZERS).dispatch(Bonus::getSerializer, RegistrySerializer::streamCodec);

    /**
     * called whenever a LivingEntity equips an item with this bonus
     * @param living the entity this bonus applied to
     */
    default void onApply(LivingEntity living) {
    }

    @Override
    default void onUse() {

    }

    @Nullable
    @Override
    default Cooldown getCooldown() {
        return null;
    }

    RegistrySerializer<T> getSerializer();

    /**
     * @param tickCount the count of ticks since this bonus has been activated
     * @param living the entity the bonus is applied to
     * @return if this tick should apply a tick (similar to how the actual {@link net.minecraft.world.effect.MobEffect MobEffect} works)
     */
    default boolean isEffectTick(int tickCount, LivingEntity living) {
        return false;
    }

    /**
     * applied each tick that {@link Bonus#isEffectTick(int, LivingEntity)} returns true
     * only called serverside
     * @param tickCount count of ticks since this bonus has been activated
     * @param entity the entity the bonus is applied to
     */
    default void onTick(int tickCount, @NotNull LivingEntity entity) {
    }

    /**
     * @param killed the entity that has been killed
     * @param user the entity that killed the target and owner of this bonus
     * @param type the damage type that was used to kill this entity
     */
    default void onEntityKilled(LivingEntity killed, LivingEntity user, MiscHelper.DamageType type) {
    }


    /**
     * called whenever an entity un-equips this bonus
     * @param living the entity this bonus was previously applied to
     */
    default void onRemove(LivingEntity living) {
    }

    /**
     * @param living the entity applied to
     * @return all attribute modifiers this bonus should apply to the given entity
     */
    default @Nullable Multimap<Holder<Attribute>, AttributeModifier> getModifiers(LivingEntity living) {return null;}

    /**
     * @param attacked the attack target
     * @param attacker the attacker and source entity of this bonus
     * @param type damage type of the attack
     * @param damage amount of damage dealt
     * @return the (potentially) modified damage value
     */
    default float onEntityHurt(LivingEntity attacked, LivingEntity attacker, MiscHelper.DamageType type, float damage) {
        return damage;
    }


    /**
     * @param attacked the attack target and source entity of this bonus
     * @param attacker the attacker
     * @param type damage type of the attack
     * @param damage amount of damage dealt
     * @return the (potentially) modified damage value
     */
    default float onTakeDamage(LivingEntity attacked, LivingEntity attacker, MiscHelper.DamageType type, float damage) {
        return damage;
    }
}