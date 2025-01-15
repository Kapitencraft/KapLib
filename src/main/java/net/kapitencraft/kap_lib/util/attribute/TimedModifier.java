package net.kapitencraft.kap_lib.util.attribute;

import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeModifier;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaAttributeModifierTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimedModifier extends AttributeModifier implements IKapLibAttributeModifier {
    public static final Codec<TimedModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("Name").forGetter(AttributeModifier::getName),
            Codec.DOUBLE.fieldOf("Amount").forGetter(AttributeModifier::getAmount),
            VanillaAttributeModifierTypes.OPERATION_CODEC.fieldOf("Operation").forGetter(AttributeModifier::getOperation),
            Codec.INT.fieldOf("Timer").forGetter(TimedModifier::remaining)
    ).apply(instance, TimedModifier::new));

    private int timer;


    public TimedModifier(String name, double value, Operation operation, int timer) {
        super(UUID.randomUUID(), name, value, operation);
        this.timer = timer;
    }

    private boolean tickDown() {
        return this.timer-- <= 0;
    }

    private int remaining() {
        return timer;
    }

    @Override
    public Codec<? extends AttributeModifier> getCodec() {
        return CODEC;
    }

    @Override
    public boolean tickBased() {
        return true;
    }

    @Override
    public boolean tick() {
        return tickDown();
    }
}
