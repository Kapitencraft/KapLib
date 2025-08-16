package net.kapitencraft.kap_lib.item.bonus.type;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
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

    private static final Codec<EffectsBonus> CODEC = MobEffectInstance.CODEC.listOf().xmap(EffectsBonus::new, EffectsBonus::getEffects);

    private static final StreamCodec<RegistryFriendlyByteBuf, EffectsBonus> STREAM_CODEC = ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC).map(EffectsBonus::new, EffectsBonus::getEffects);

    public static final DataPackSerializer<EffectsBonus> SERIALIZER = new DataPackSerializer<>(CODEC, STREAM_CODEC);

    public EffectsBonus(List<MobEffectInstance> effects) {
        this.effects.addAll(effects);
    }

    private List<MobEffectInstance> getEffects() {
        return effects;
    }

    private final List<MobEffectInstance> effects = new ArrayList<>();

    @Override
    public DataPackSerializer<EffectsBonus> getSerializer() {
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
