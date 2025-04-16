package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.collection.BiCollectors;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class SetAttributesFunction extends SpawnEntityConditionalFunction {
    private final Pair<Attribute, Modifiers>[] data;
    private static final JsonSerializer<AttributeModifier> SERIALIZER = new JsonSerializer<>(ExtraCodecs.ATTRIBUTE_MODIFIER);

    private SetAttributesFunction(LootItemCondition[] pPredicates, Pair<Attribute, Modifiers>[] data) {
        super(pPredicates);
        this.data = data;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (pEntity instanceof LivingEntity living) {
            for (Pair<Attribute, Modifiers> pair : data) {
                try {
                    pair.getSecond().apply(living.getAttribute(pair.getFirst()));
                } catch (Exception e) {
                    KapLibMod.LOGGER.warn(Markers.SPAWN_TABLE_MANAGER, "unable to apply attribute modifiers for '{}': {}", ForgeRegistries.ATTRIBUTES.getKey(pair.getFirst()), e.getMessage());
                }
            }
        } else logWrongType("LivingEntity", pEntity);
        return pEntity;
    }

    @Override
    public SpawnEntityFunctionType getType() {
        return SpawnEntityFunctions.SET_ATTRIBUTES.get();
    }

    private record Modifiers(AttributeModifier[] modifiers, Double base) {

        public void apply(AttributeInstance instance) {
            if (base != null) instance.setBaseValue(this.base);
            for (AttributeModifier modifier : modifiers) {
                instance.addPermanentModifier(modifier);
            }
        }

        public static Modifiers fromJson(JsonElement element) {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                AttributeModifier[] modifiers = object.has("modifiers") ?
                        JsonHelper.castToObjects(GsonHelper.getAsJsonArray(object, "modifiers"))
                                .map(SERIALIZER::deserialize).toArray(AttributeModifier[]::new) :
                        new AttributeModifier[0];
                Double base = object.has("base") ? GsonHelper.getAsDouble(object, "base") : null;
                return new Modifiers(modifiers, base);
            } else throw new JsonParseException("Modifiers data was no object");
        }

        private static AttributeModifier modifierFromJson(JsonObject element) {
            UUID uuid = element.has("uuid") ? UUID.fromString(GsonHelper.getAsString(element, "uuid")) : UUID.randomUUID();
            String name = GsonHelper.getAsString(element, "name");
            double amount = GsonHelper.getAsDouble(element, "amount");
            AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(GsonHelper.getAsString(element, "operation").toUpperCase());
            return new AttributeModifier(uuid, name, amount, operation);
        }

        public JsonObject toJson() {
            JsonObject object = new JsonObject();
            if (this.modifiers.length > 0) {
                JsonArray array = new JsonArray();
                for (AttributeModifier modifier : this.modifiers) {
                    array.add(SERIALIZER.serialize(modifier));
                }
                object.add("modifiers", array);
            }
            if (this.base != null) object.addProperty("base", this.base);
            return object;
        }
    }

    public static class Serializer extends SpawnEntityConditionalFunction.Serializer<SetAttributesFunction> {

        @Override
        public void serialize(@NotNull JsonObject pJson, @NotNull SetAttributesFunction pFunction, @NotNull JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pFunction, pSerializationContext);
            pJson.add("modifiers", Arrays.stream(pFunction.data).collect(CollectorHelper
                                    .toMapStream(Pair::getFirst, Pair::getSecond)
                            ).mapKeys(ForgeRegistries.ATTRIBUTES::getKey)
                    .mapKeys(ResourceLocation::toString)
                    .mapValues(Modifiers::toJson).collect(BiCollectors.mergeJson())
            );
        }

        @Override
        public SetAttributesFunction deserialize(JsonObject pObject, JsonDeserializationContext pDeserializationContext, LootItemCondition[] pConditions) {
            Pair<Attribute, Modifiers>[] data = MapStream.of(GsonHelper.getAsJsonObject(pObject, "modifiers").asMap())
                    .mapValues(Modifiers::fromJson)
                    .mapKeys(ResourceLocation::new)
                    .mapKeys(ForgeRegistries.ATTRIBUTES::getValue)
                    .toPairArray();
            return new SetAttributesFunction(pConditions, data);
        }
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder extends SpawnEntityConditionalFunction.Builder<Builder> {
        private final Map<Attribute, ModifiersBuilder> modifiers = new HashMap<>();

        @Override
        protected Builder getThis() {
            return this;
        }

        public ModifiersBuilder withAttribute(Attribute attribute) {
            modifiers.putIfAbsent(attribute, new ModifiersBuilder());
            return modifiers.get(attribute);
        }

        @Override
        public SpawnEntityFunction build() {
            return new SetAttributesFunction(getConditions(), makePairs());
        }

        private Pair<Attribute, Modifiers>[] makePairs() {
            return MapStream.of(modifiers).mapValues(ModifiersBuilder::createModifiers).toPairArray();
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
                return new Modifiers(this.modifiers.toArray(AttributeModifier[]::new), this.base);
            }

            public Builder end() {
                return Builder.this;
            }
        }
    }
}
