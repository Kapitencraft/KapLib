package net.kapitencraft.kap_lib.bonus;

import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface IEventListener {

    @Deprecated
    void onUse();

    void onTick(int tickCount, @NotNull LivingEntity entity);

    void onApply(LivingEntity living);

    void onRemove(LivingEntity living);

    @Nullable
    Cooldown getCooldown();

    @Nullable
    Multimap<Holder<Attribute>, AttributeModifier> getModifiers(LivingEntity living);

    void onEntityKilled(LivingEntity killed, LivingEntity user, MiscHelper.DamageType type);

    float onEntityHurt(LivingEntity hurt, LivingEntity user, MiscHelper.DamageType type, float damage);

    float onTakeDamage(LivingEntity hurt, LivingEntity source, MiscHelper.DamageType type, float damage);
}
