package net.kapitencraft.kap_lib.item.bonus.type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.item.modifier_display.EquipmentDisplayExtension;
import net.kapitencraft.kap_lib.registry.ExtraCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class AttributeModifiersBonus implements Bonus<AttributeModifiersBonus>, EquipmentDisplayExtension {
    private static final Codec<Multimap<Holder<Attribute>, AttributeModifier>> ENTRIES_CODEC = Codec.unboundedMap(BuiltInRegistries.ATTRIBUTE.holderByNameCodec(), ExtraCodecs.ATTRIBUTE_MODIFIER.listOf()).xmap(CollectionHelper::fromListMap, CollectionHelper::fromMultimap);
    private static final Codec<AttributeModifiersBonus> CODEC = RecordCodecBuilder.create(attributeModifiersBonusInstance -> attributeModifiersBonusInstance.group(
            ENTRIES_CODEC.fieldOf("entries").forGetter(AttributeModifiersBonus::getModifiers),
            Type.CODEC.optionalFieldOf("bracket_type", Type.NONE).forGetter(AttributeModifiersBonus::getType),
            ExtraCodecs.EFFECT_SERIALIZING_STYLE.optionalFieldOf("style", Style.EMPTY).forGetter(AttributeModifiersBonus::getStyle)
    ).apply(attributeModifiersBonusInstance, AttributeModifiersBonus::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeModifiersBonus> STREAM_CODEC = StreamCodec.composite(

    );

    public static final DataPackSerializer<AttributeModifiersBonus> SERIALIZER = new DataPackSerializer<>(
            CODEC,
            STREAM_CODEC
    );

    private final Multimap<Holder<Attribute>, AttributeModifier> modifiers;
    private final Type type;
    private final Style style;

    public AttributeModifiersBonus(Multimap<Holder<Attribute>, AttributeModifier> modifiers, Type type, Style style) {
        this.modifiers = modifiers;
        this.type = type;
        this.style = style;
    }

    private Multimap<Holder<Attribute>, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public @NotNull Multimap<Holder<Attribute>, AttributeModifier> getModifiers(EquipmentSlot slot) {
        return modifiers;
    }

    @Override
    public DataPackSerializer<AttributeModifiersBonus> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @Nullable Multimap<Holder<Attribute>, AttributeModifier> getModifiers(LivingEntity living) {
        return modifiers;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Style getStyle() {
        return style;
    }

    @Override
    public Type getType() {
        return type;
    }

    public static class Builder {
        private final Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
        private Type type = Type.NONE;
        private Style style = Style.EMPTY;

        public Builder addModifier(Holder<Attribute> attribute, AttributeModifier modifier) {
            this.modifiers.put(attribute, modifier);
            return this;
        }

        public Builder addModifier(Holder<Attribute> attribute, ResourceLocation name, double amount, AttributeModifier.Operation operation) {
            return this.addModifier(attribute, new AttributeModifier(name, amount, operation));
        }

        public Builder setBracketType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setDisplayStyle(UnaryOperator<Style> style) {
            this.style = style.apply(Style.EMPTY);
            return this;
        }

        public AttributeModifiersBonus build() {
            return new AttributeModifiersBonus(ImmutableMultimap.copyOf(modifiers), type, style);
        }
    }
}