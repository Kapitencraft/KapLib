package net.kapitencraft.kap_lib.item.bonus.type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.item.modifier_display.ItemModifiersDisplayExtension;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class AttributeModifiersBonus implements Bonus<AttributeModifiersBonus>, ItemModifiersDisplayExtension {
    private static final Codec<Multimap<Attribute, AttributeModifier>> ENTRIES_CODEC = Codec.unboundedMap(ForgeRegistries.ATTRIBUTES.getCodec(), ExtraCodecs.ATTRIBUTE_MODIFIER.listOf()).xmap(CollectionHelper::fromListMap, CollectionHelper::fromMultimap);
    private static final Codec<AttributeModifiersBonus> CODEC = RecordCodecBuilder.create(attributeModifiersBonusInstance -> attributeModifiersBonusInstance.group(
            ENTRIES_CODEC.fieldOf("entries").forGetter(AttributeModifiersBonus::getModifiers),
            Type.CODEC.fieldOf("bracket_type").forGetter(AttributeModifiersBonus::getType),
            ExtraCodecs.EFFECT_SERIALIZING_STYLE.fieldOf("style").forGetter(AttributeModifiersBonus::getStyle)
    ).apply(attributeModifiersBonusInstance, AttributeModifiersBonus::new));

    public static final DataPackSerializer<AttributeModifiersBonus> SERIALIZER = new DataPackSerializer<>(
            CODEC,
            AttributeModifiersBonus::fromNetwork,
            AttributeModifiersBonus::toNetwork
    );

    private final Multimap<Attribute, AttributeModifier> modifiers;
    private final Type type;
    private final Style style;

    public AttributeModifiersBonus(Multimap<Attribute, AttributeModifier> modifiers, Type type, Style style) {
        this.modifiers = modifiers;
        this.type = type;
        this.style = style;
    }

    private Multimap<Attribute, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot) {
        return modifiers;
    }

    @Override
    public DataPackSerializer<AttributeModifiersBonus> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @Nullable Multimap<Attribute, AttributeModifier> getModifiers(LivingEntity living) {
        return modifiers;
    }

    private static AttributeModifiersBonus fromNetwork(FriendlyByteBuf buf) {
        return new AttributeModifiersBonus(NetworkHelper.readMultimap(buf,
                buf1 -> buf1.readRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES),
                buf1 -> buf1.readJsonWithCodec(ExtraCodecs.ATTRIBUTE_MODIFIER)
        ), buf.readEnum(Type.class), buf.readJsonWithCodec(ExtraCodecs.EFFECT_SERIALIZING_STYLE));
    }

    private static void toNetwork(FriendlyByteBuf buf, AttributeModifiersBonus bonus) {
        NetworkHelper.writeMultimap(buf, bonus.modifiers,
                (buf1, attribute) -> buf1.writeRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES, attribute),
                (buf1, modifier) -> buf1.writeJsonWithCodec(ExtraCodecs.ATTRIBUTE_MODIFIER, modifier)
        );
        buf.writeEnum(bonus.type);
        buf.writeJsonWithCodec(ExtraCodecs.EFFECT_SERIALIZING_STYLE, bonus.style);
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
        private final Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        private Type type;
        private Style style;

        public Builder addModifier(Attribute attribute, AttributeModifier modifier) {
            this.modifiers.put(attribute, modifier);
            return this;
        }

        public Builder addModifier(Supplier<? extends Attribute> supplier, AttributeModifier modifier) {
            return this.addModifier(supplier.get(), modifier);
        }

        public Builder addModifier(Attribute attribute, String name, double amount, AttributeModifier.Operation operation) {
            return this.addModifier(attribute, new AttributeModifier(UUID.randomUUID(), name, amount, operation));
        }

        public Builder addModifier(Supplier<? extends Attribute> supplier, String name, double amount, AttributeModifier.Operation operation) {
            return this.addModifier(supplier.get(), name, amount, operation);
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