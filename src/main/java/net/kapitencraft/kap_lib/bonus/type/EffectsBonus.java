package net.kapitencraft.kap_lib.bonus.type;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.kapitencraft.kap_lib.bonus.Bonus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * adds one or more effect to the player
 * recommended to use at least 30 ticks because of flickering
 */
public class EffectsBonus implements Bonus<EffectsBonus> {

    private static final MapCodec<EffectsBonus> CODEC = MobEffectInstance.CODEC.listOf().xmap(EffectsBonus::new, EffectsBonus::getEffects).fieldOf("effects");

    private static final StreamCodec<RegistryFriendlyByteBuf, EffectsBonus> STREAM_CODEC = MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()).map(EffectsBonus::new, EffectsBonus::getEffects);

    public static final RegistrySerializer<EffectsBonus> SERIALIZER = new RegistrySerializer<>(CODEC, STREAM_CODEC);

    public EffectsBonus(List<MobEffectInstance> effects) {
        this.effects.addAll(effects);
    }

    private List<MobEffectInstance> getEffects() {
        return effects;
    }

    private final List<MobEffectInstance> effects = new ArrayList<>();

    @Override
    public RegistrySerializer<EffectsBonus> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public void onTick(int tickCount, @NotNull LivingEntity entity) {
        this.effects.stream()
                .map(MobEffectInstance::new) //create new Object to effectively finalize 'effects'
                .forEach(entity::addEffect);
    }

    @Override
    public boolean isEffectTick(int tickCount, LivingEntity living) {
        return true;
    }
}
