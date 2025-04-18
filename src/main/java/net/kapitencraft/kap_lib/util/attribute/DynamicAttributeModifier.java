package net.kapitencraft.kap_lib.util.attribute;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;

/**
 * wrapper class for dynamic attribute modifiers that depend on a LivingEntity
 */
public class DynamicAttributeModifier extends AttributeModifier implements IKapLibAttributeModifier {
    private final LivingEntity living;
    private final Function<LivingEntity, Double> provider;

    public DynamicAttributeModifier(UUID id, String name, Operation operation, LivingEntity living, Function<LivingEntity, Double> provider) {
        super(id, name, 0, operation);
        this.living = living;
        this.provider = provider;
    }

    /**
     * @return the value provided by the {@link DynamicAttributeModifier#provider provider}
     */
    @Override
    public double getAmount() {
        return provider.apply(living);
    }

    @Override
    public @NotNull CompoundTag save() {
        throw new IllegalStateException("should not save changing attributeModifier");
    }

    @Override
    public Codec<? extends AttributeModifier> getCodec() {
        return null;
    }

    @Override
    public boolean tickBased() {
        return false;
    }

    @Override
    public boolean tick() {
        return false;
    }
}
