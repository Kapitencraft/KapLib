package net.kapitencraft.kap_lib.item.bonus.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * adds one or more effect to the player
 * recommended to use at least 30 ticks because of flickering
 */
public class EffectsBonus implements Bonus<EffectsBonus> {
    private static final Codec<MobEffectInstance> EFFECT_INSTANCE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
                    Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
                    Codec.INT.fieldOf("amplifier").forGetter(MobEffectInstance::getAmplifier)
            ).apply(instance, MobEffectInstance::new)
    );

    private static final Codec<EffectsBonus> CODEC = EFFECT_INSTANCE_CODEC.listOf().xmap(EffectsBonus::new, EffectsBonus::getEffects);

    public static final DataPackSerializer<EffectsBonus> SERIALIZER = new DataPackSerializer<>(CODEC, EffectsBonus::fromNetwork, EffectsBonus::toNetwork);

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

    private static void writeEffect(FriendlyByteBuf buf, MobEffectInstance instance) {
        buf.writeRegistryId(ForgeRegistries.MOB_EFFECTS, instance.getEffect());
        buf.writeInt(instance.getDuration());
        buf.writeInt(instance.getAmplifier());
        //other information ignored
    }

    public static EffectsBonus fromNetwork(FriendlyByteBuf buf) {
        return new EffectsBonus(buf.readCollection(ArrayList::new, EffectsBonus::readEffect));
    }

    private static void toNetwork(FriendlyByteBuf buf, EffectsBonus bonus) {
        buf.writeCollection(bonus.effects, EffectsBonus::writeEffect);
    }

    private static MobEffectInstance readEffect(FriendlyByteBuf buf) {
        MobEffect effect = buf.readRegistryId();
        int duration = buf.readInt();
        int amplifier = buf.readInt();
        return new MobEffectInstance(effect, duration, amplifier);
    }
}
