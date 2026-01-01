package net.kapitencraft.kap_lib.bonus.type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.bonus.Bonus;
import net.kapitencraft.kap_lib.core.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.core.io.serialization.RegistrySerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * a bonus that modifies attributes in some way
 * all modifiers are expected to use the same location
 */
public class AttributeModifiersBonus implements Bonus<AttributeModifiersBonus> {
    private static final Codec<Multimap<Holder<Attribute>, AttributeModifier>> ENTRIES_CODEC = Codec.unboundedMap(BuiltInRegistries.ATTRIBUTE.holderByNameCodec(), AttributeModifier.CODEC.listOf()).xmap(CollectionHelper::fromListMap, CollectionHelper::fromMultimap);
    private static final MapCodec<AttributeModifiersBonus> CODEC = RecordCodecBuilder.mapCodec(attributeModifiersBonusInstance -> attributeModifiersBonusInstance.group(
            ENTRIES_CODEC.fieldOf("entries").forGetter(AttributeModifiersBonus::getModifiers)
    ).apply(attributeModifiersBonusInstance, AttributeModifiersBonus::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeModifiersBonus> STREAM_CODEC = StreamCodec.composite(
            ExtraStreamCodecs.multimap(ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE), AttributeModifier.STREAM_CODEC), AttributeModifiersBonus::getModifiers,
            AttributeModifiersBonus::new
    );

    public static final RegistrySerializer<AttributeModifiersBonus> SERIALIZER = new RegistrySerializer<>(
            CODEC,
            STREAM_CODEC
    );

    private final Multimap<Holder<Attribute>, AttributeModifier> modifiers;
    private final ResourceLocation location;

    public AttributeModifiersBonus(Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
        this.modifiers = modifiers;
        Optional<AttributeModifier> first = modifiers.values().stream().findFirst();
        if (first.isPresent()) {
            location = first.get().id();
        } else {
            throw new IllegalStateException("no modifier in modifier bonus!");
        }
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public RegistrySerializer<AttributeModifiersBonus> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @Nullable Multimap<Holder<Attribute>, AttributeModifier> getModifiers(LivingEntity living) {
        return modifiers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public static class Builder {
        private final Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();

        public Builder addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
            this.modifiers.put(attribute, modifier);
            return this;
        }

        public Builder addModifier(Holder<Attribute> attribute, ResourceLocation name, double amount, AttributeModifier.Operation operation) {
            return this.addModifier(attribute, new AttributeModifier(name, amount, operation));
        }

        public AttributeModifiersBonus build() {
            return new AttributeModifiersBonus(ImmutableMultimap.copyOf(modifiers));
        }
    }
}