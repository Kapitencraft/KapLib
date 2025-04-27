package net.kapitencraft.kap_lib.requirements.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.CountCondition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Supplier;

/**
 * used for custom Stat Types to add custom display-translations
 */
public class CustomStatReqCondition extends CountCondition<CustomStatReqCondition> {
    private static final Codec<Stat<ResourceLocation>> STAT_CODEC = ResourceLocation.CODEC.xmap(Stats.CUSTOM::get, Stat::getValue);

    private static final Codec<CustomStatReqCondition> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    STAT_CODEC.fieldOf("stat").forGetter(i -> i.stat),
                    Codec.INT.fieldOf("amount").forGetter(i -> i.minLevel),
                    ExtraCodecs.COMPONENT.fieldOf("display").forGetter(CustomStatReqCondition::display)
            ).apply(instance, CustomStatReqCondition::create)
    );

    public static DataPackSerializer<CustomStatReqCondition> SERIALIZER = new DataPackSerializer<>(
            CODEC, CustomStatReqCondition::fromNetwork, CustomStatReqCondition::toNetwork
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

    private static void toNetwork(FriendlyByteBuf buf, CustomStatReqCondition condition) {
        buf.writeResourceLocation(condition.stat.getValue());
        buf.writeInt(condition.minLevel);
        buf.writeComponent(condition.component);
    }

    public static CustomStatReqCondition fromNetwork(FriendlyByteBuf buf) {
        return new CustomStatReqCondition(
                Stats.CUSTOM.get(buf.readResourceLocation()),
                buf.readInt(),
                buf.readComponent()
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
