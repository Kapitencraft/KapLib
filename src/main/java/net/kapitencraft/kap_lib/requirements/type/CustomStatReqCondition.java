package net.kapitencraft.kap_lib.requirements.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Supplier;

/**
 * used for custom Stat Types to add custom display-translations
 */
public class CustomStatReqCondition extends StatReqCondition {
    private static final Codec<CustomStatReqCondition> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    STAT_CODEC.fieldOf("stat").forGetter(i -> i.stat),
                    Codec.INT.fieldOf("amount").forGetter(i -> i.minLevel),
                    ExtraCodecs.COMPONENT.fieldOf("display").forGetter(CustomStatReqCondition::display)
            ).apply(instance, CustomStatReqCondition::create)
    );

    private static CustomStatReqCondition create(Stat<?> stat, Integer integer, Component component) {
        return new CustomStatReqCondition((Stat<ResourceLocation>) stat, integer, component);
    }

    private final Component component;

    private CustomStatReqCondition(Stat<ResourceLocation> stat, int level, Component component) {
        super(stat, level);
        this.component = component;
    }

    public CustomStatReqCondition(Stat<ResourceLocation> stat, int level, String translateKey) {
        this(stat, level, Component.translatable(translateKey, level));
    }

    public CustomStatReqCondition(Supplier<ResourceLocation> statSup, int level, String translateKey) {
        this(Stats.CUSTOM.get(statSup.get()), level, translateKey);
    }

    @Override
    public Component getCountedDisplay() {
        return component;
    }
}
