package net.kapitencraft.kap_lib.requirements.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.CountCondition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;

import java.util.function.Supplier;

/**
 * used for custom Stat Types to add custom display-translations
 */
public class CustomStatReqCondition extends CountCondition<CustomStatReqCondition> {
    private static final Codec<Stat<ResourceLocation>> STAT_CODEC = BuiltInRegistries.CUSTOM_STAT.byNameCodec().xmap(Stats.CUSTOM::get, Stat::getValue);

    private static final Codec<CustomStatReqCondition> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    STAT_CODEC.fieldOf("stat").forGetter(i -> i.stat),
                    Codec.INT.fieldOf("amount").forGetter(i -> i.minLevel),
                    ComponentSerialization.CODEC.fieldOf("display").forGetter(CustomStatReqCondition::display)
            ).apply(instance, CustomStatReqCondition::create)
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, CustomStatReqCondition> STREAM_CODEC = StreamCodec.of(CustomStatReqCondition::toNetwork, CustomStatReqCondition::fromNetwork);

    public static DataPackSerializer<CustomStatReqCondition> SERIALIZER = new DataPackSerializer<>(
            CODEC, STREAM_CODEC
    );

    private static CustomStatReqCondition create(Stat<ResourceLocation> stat, Integer integer, Component component) {
        return new CustomStatReqCondition(stat, integer, component);
    }

    private final Stat<ResourceLocation> stat;
    private final Component component;

    private CustomStatReqCondition(Stat<ResourceLocation> stat, int level, Component component) {
        super(StatReqCondition.createCountExtractor(stat), level);
        this.stat = stat;
        this.component = component;
    }

    public CustomStatReqCondition(Stat<ResourceLocation> stat, int level, String translateKey) {
        this(stat, level, Component.translatable(translateKey, level));
    }

    public CustomStatReqCondition(Supplier<ResourceLocation> statSup, int level, String translateKey) {
        this(Stats.CUSTOM.get(statSup.get()), level, translateKey);
    }

    @SuppressWarnings("DataFlowIssue")
    private static void toNetwork(RegistryFriendlyByteBuf buf, CustomStatReqCondition condition) {
        buf.writeResourceLocation(BuiltInRegistries.CUSTOM_STAT.getKey(condition.stat.getValue()));
        buf.writeInt(condition.minLevel);
        ComponentSerialization.STREAM_CODEC.encode(buf, condition.component);
    }

    @SuppressWarnings("DataFlowIssue")
    public static CustomStatReqCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        return new CustomStatReqCondition(
                Stats.CUSTOM.get(BuiltInRegistries.CUSTOM_STAT.get(buf.readResourceLocation())),
                buf.readInt(),
                ComponentSerialization.STREAM_CODEC.decode(buf)
        );
    }

    @Override
    public DataPackSerializer<CustomStatReqCondition> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public Component getCountedDisplay() {
        return component;
    }
}
