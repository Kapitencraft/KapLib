package net.kapitencraft.kap_lib.item.bonus;

import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.io.serialization.IDataGenElement;
import net.kapitencraft.kap_lib.item.IEventListener;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public interface Bonus<T extends Bonus<T>> extends IDataGenElement<T>, IEventListener {

    static <T extends Bonus<T>> DataPackSerializer<T> createSerializer(Codec<T> codec, FriendlyByteBuf.Reader<T> factory) {
        return IDataGenElement.createSerializer(codec, factory);
    }

    @Override
    default void toNetwork(FriendlyByteBuf buf) {
        buf.writeRegistryId(ExtraRegistries.BONUS_SERIALIZER, this.getSerializer());
        additionalToNetwork(buf);
    }

    @Override
    default JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add("data", getSerializer().serialize((T) this));
        object.addProperty("type", Objects.requireNonNull(ExtraRegistries.BONUS_SERIALIZER.getKey(this.getSerializer()), String.format("unknown requirement type: %s", this.getClass().getCanonicalName())).toString());
        return object;
    }

    /**
     * called whenever a LivingEntity equips this bonus
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

    DataPackSerializer<T> getSerializer();

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
    default @Nullable Multimap<Attribute, AttributeModifier> getModifiers(LivingEntity living) {return null;}

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

    void addDisplay(List<Component> currentTooltip);
}