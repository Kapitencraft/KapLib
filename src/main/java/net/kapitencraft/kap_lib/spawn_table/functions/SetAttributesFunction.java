package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.kapitencraft.kap_lib.registry.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.*;

public final class SetAttributesFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<SetAttributesFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.pair(Attribute.CODEC, Modifiers.CODEC).listOf().fieldOf("modifiers").forGetter(f -> f.data)
    ).and(commonFields(i).t1()).apply(i, SetAttributesFunction::new));

    private final List<Pair<Holder<Attribute>, Modifiers>> data;
    private static final JsonSerializer<AttributeModifier> SERIALIZER = new JsonSerializer<>(ExtraCodecs.ATTRIBUTE_MODIFIER);

    private SetAttributesFunction(List<Pair<Holder<Attribute>, Modifiers>> data, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        this.data = data;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof LivingEntity living) {
            for (Pair<Holder<Attribute>, Modifiers> pair : data) {
                try {
                    pair.getSecond().apply(living.getAttribute(pair.getFirst()));
                } catch (Exception e) {
                    KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "unable to apply attribute modifiers for '{}': {}", pair.getFirst().getKey().location(), e.getMessage());
                }
            }
        } else logWrongType("LivingEntity", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.SET_ATTRIBUTES.get();
    }

    private record Modifiers(List<AttributeModifier> modifiers, Double base) {
        public static final Codec<Modifiers> CODEC = RecordCodecBuilder.create(i -> i.group(
                AttributeModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(Modifiers::modifiers),
                Codec.DOUBLE.optionalFieldOf("base").forGetter(f -> Optional.ofNullable(f.base))
        ).apply(i, Modifiers::fromCodec));

        private static Modifiers fromCodec(List<AttributeModifier> attributeModifiers, Optional<Double> aDouble) {
            return new Modifiers(attributeModifiers, aDouble.orElse(null));
        }

        public void apply(AttributeInstance instance) {
            if (base != null) instance.setBaseValue(this.base);
            for (AttributeModifier modifier : modifiers) {
                instance.addPermanentModifier(modifier);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final Map<Holder<Attribute>, ModifiersBuilder> modifiers = new HashMap<>();

        @Override
        protected Builder getThis() {
            return this;
        }

        public ModifiersBuilder withAttribute(Holder<Attribute> attribute) {
            modifiers.putIfAbsent(attribute, new ModifiersBuilder());
            return modifiers.get(attribute);
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetAttributesFunction(makePairs(), getConditions());
        }

        private List<Pair<Holder<Attribute>, Modifiers>> makePairs() {
            return MapStream.of(modifiers).mapValues(ModifiersBuilder::createModifiers).toPairList();
        }

        public class ModifiersBuilder {
            private Double base = null;
            private final List<AttributeModifier> modifiers = new ArrayList<>();

            public ModifiersBuilder setBase(double base) {
                this.base = base;
                return this;
            }

            public ModifiersBuilder addModifier(AttributeModifier modifier) {
                this.modifiers.add(modifier);
                return this;
            }

            private Modifiers createModifiers() {
                return new Modifiers(this.modifiers, this.base);
            }

            public Builder end() {
                return Builder.this;
            }
        }
    }
}
