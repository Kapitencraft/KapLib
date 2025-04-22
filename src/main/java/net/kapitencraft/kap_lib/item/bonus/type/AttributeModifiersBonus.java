package net.kapitencraft.kap_lib.item.bonus.type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.helpers.NetworkHelper;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class AttributeModifiersBonus implements Bonus<AttributeModifiersBonus> {
    private static final Codec<Multimap<Attribute, AttributeModifier>> ENTRIES_CODEC = Codec.unboundedMap(ForgeRegistries.ATTRIBUTES.getCodec(), ExtraCodecs.ATTRIBUTE_MODIFIER.listOf()).xmap(CollectionHelper::fromListMap, CollectionHelper::fromMultimap);
    private static final Codec<AttributeModifiersBonus> CODEC = ENTRIES_CODEC.xmap(AttributeModifiersBonus::new, AttributeModifiersBonus::getModifiers);

    public static final DataPackSerializer<AttributeModifiersBonus> SERIALIZER = new DataPackSerializer<>(
            CODEC, () -> new AttributeModifiersBonus(ImmutableMultimap.of()),
            AttributeModifiersBonus::fromNetwork,
            AttributeModifiersBonus::toNetwork
    );

    private final Multimap<Attribute, AttributeModifier> modifiers;

    public AttributeModifiersBonus(Multimap<Attribute, AttributeModifier> modifiers) {
        this.modifiers = modifiers;
    }


    private Multimap<Attribute, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public DataPackSerializer<AttributeModifiersBonus> getSerializer() {
        return SERIALIZER;
    }

    //TODO implement
    @Override
    public @Nullable Multimap<Attribute, AttributeModifier> getModifiers(LivingEntity living) {
        return modifiers;
    }

    @Override
    public void addDisplay(List<Component> currentTooltip) {

    }

    private static AttributeModifiersBonus fromNetwork(FriendlyByteBuf buf) {
        return new AttributeModifiersBonus(NetworkHelper.readMultimap(buf,
                buf1 -> buf1.readRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES),
                buf1 -> buf1.readJsonWithCodec(ExtraCodecs.ATTRIBUTE_MODIFIER)
        ));
    }

    private static void toNetwork(FriendlyByteBuf buf, AttributeModifiersBonus bonus) {
        NetworkHelper.writeMultimap(buf, bonus.modifiers,
                (buf1, attribute) -> buf1.writeRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES, attribute),
                (buf1, modifier) -> buf1.writeJsonWithCodec(ExtraCodecs.ATTRIBUTE_MODIFIER, modifier)
        );
    }

    public static class Builder {
        private final Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();

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

        public AttributeModifiersBonus build() {
            return new AttributeModifiersBonus(ImmutableMultimap.copyOf(modifiers));
        }
    }
}