package net.kapitencraft.kap_lib.helpers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.util.attribute.DynamicAttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface AttributeHelper {

    /**
     * gets the players experience scale, which should be multiplied with the base experience to get the final dropped experience
     */
    static double getExperienceScale(Player player) {
        double wisdom = player.getAttributeValue(ExtraAttributes.WISDOM.get());
        return 1 + (wisdom / 100);
    }

    /**
     * increases the value of all modifiers matching the predicate (Operations, Attribute) by {@code percent} percent (in decimal)
     * @param multimap {@link Multimap} to increase
     * @param percent percentage to increase all multimap contents with filter to operations and attributeReq
     * @param operations filter for {@link AttributeModifier.Operation}s
     * @param attributeReq filter to only increase param's {@link Attribute}
     * @return the multimap with all increased values
     */
    static Multimap<Attribute, AttributeModifier> increaseByPercent(Multimap<Attribute, AttributeModifier> multimap, double percent, AttributeModifier.Operation[] operations, @Nullable Attribute attributeReq) {
        HashMultimap<Attribute, AttributeModifier> toReturn = HashMultimap.create();
        Collection<AttributeModifier> attributeModifiers;
        for (Attribute attribute : multimap.keys()) {
            if (attributeReq == null || attribute == attributeReq) {
                attributeModifiers = multimap.get(attribute);
                for (AttributeModifier modifier : attributeModifiers) {
                    if (CollectionHelper.arrayContains(operations, modifier.getOperation())) {
                        toReturn.put(attribute, copyWithValue(modifier, modifier.getAmount() * (1 + percent)));
                    } else {
                        toReturn.put(attribute, modifier);
                    }
                }
            } else {
                attributeModifiers = multimap.get(attribute);
                for (AttributeModifier modifier : attributeModifiers) {
                    toReturn.put(attribute, modifier);
                }
            }
        }
        return toReturn;
    }


    /**
     * merge {@code modifier} into the modifiers given as {@code multimap}
     * @param multimap the map to add to
     * @param attributeReq if there's a requirement for the attribute
     * @return the merged map
     */
    static Multimap<Attribute, AttributeModifier> increaseByAmount(Multimap<Attribute, AttributeModifier> multimap, Attribute attributeReq, AttributeModifier modifier) {
        HashMultimap<Attribute, AttributeModifier> toReturn = HashMultimap.create();
        boolean hasBeenAdded = attributeReq == null;
        Collection<AttributeModifier> attributeModifiers;
        AttributeModifier.Operation operation = modifier.getOperation();
        double amount = modifier.getAmount();
        //switch operation of movement speed to multiply base since addition is to powerful
        if ((attributeReq == Attributes.MOVEMENT_SPEED || attributeReq == ForgeMod.SWIM_SPEED.get()) && operation == AttributeModifier.Operation.ADDITION) {
            operation = AttributeModifier.Operation.MULTIPLY_BASE;
            amount *= 0.01;
        }
        for (Attribute attribute : multimap.keys()) {
            attributeModifiers = multimap.get(attribute);
            for (AttributeModifier newModifier : attributeModifiers) {
                if (!hasBeenAdded && attribute == attributeReq && operation == newModifier.getOperation()) {
                    toReturn.put(attribute, copyWithValue(newModifier, newModifier.getAmount() + amount));
                    hasBeenAdded = true;
                } else {
                    toReturn.put(attribute, newModifier);
                }
            }
        }
        if (!hasBeenAdded) {
            toReturn.put(attributeReq, modifier);
        }
        return toReturn;
    }

    /**
     * merge all Modifiers given in {@code toMerge} into the {@code multimap}
     * @see AttributeHelper#increaseByAmount(Multimap, Attribute, AttributeModifier) increaseByAmount
     * @param multimap to merge into
     * @param toMerge the Map being merged into multimap
     * @return {@link Multimap} with the toMerge content added to it
     */
    static Multimap<Attribute, AttributeModifier> increaseAllByAmount(Multimap<Attribute, AttributeModifier> multimap, Map<Attribute, AttributeModifier> toMerge) {
        for (Attribute attribute : toMerge.keySet()) {
            for (AttributeModifier modifier : List.of(toMerge.get(attribute)))
                multimap = increaseByAmount(multimap, attribute, modifier);
        }
        return multimap;
    }

    /**
     * merge two attribute multimaps together
     * @see AttributeHelper#increaseAllByAmount(Multimap, Map) basic map version
     */
    static Multimap<Attribute, AttributeModifier> increaseAllByAmount(Multimap<Attribute, AttributeModifier> map, Multimap<Attribute, AttributeModifier> toMerge) {
        for (Attribute attribute : toMerge.keySet()) {
            for (AttributeModifier modifier : toMerge.get(attribute)) {
                map = increaseByAmount(map, attribute, modifier);
            }
        }
        return map;
    }


    /**
     * simple null-save attribute value method
     */
    static double getSaveAttributeValue(Attribute attribute, @Nullable LivingEntity living) {
        if (living != null && living.getAttribute(attribute) != null) {
            return living.getAttributeValue(attribute);
        }
        return 0;
    }

    /**
     * method that replaces AttributeInstance#calculateValue using a custom base Value
     * @param baseValue the base value
     * @return the value of the instance using the base value
     */
    static double getAttributeValue(@Nullable AttributeInstance instance, double baseValue) {
        if (instance == null) {
            return baseValue;
        }
        double d0 = baseValue + instance.getBaseValue();

        for (AttributeModifier attributemodifier : instance.getModifiers(AttributeModifier.Operation.ADDITION)) {
            d0 += attributemodifier.getAmount();
        }

        double d1 = d0;

        for (AttributeModifier attributeModifier1 : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_BASE)) {
            d1 += d0 * attributeModifier1.getAmount();
        }

        for (AttributeModifier attributeModifier2 : instance.getModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
            d1 *= 1.0D + attributeModifier2.getAmount();
        }
        return instance.getAttribute().sanitizeValue(d1);
    }

    /**
     * method to copy an {@link AttributeModifier}, changing its value
     * @return the copy with the new value
     */
    static AttributeModifier copyWithValue(AttributeModifier modifier, double value) {
        return new AttributeModifier(modifier.getId(), modifier.getName(), value, modifier.getOperation());
    }

    /**
     * adds a modifier that is dependent on the entity
     * @return the new {@link DynamicAttributeModifier}
     */
    static AttributeModifier addLiquidModifier(@Nullable UUID uuid, String name, AttributeModifier.Operation operation, Function<LivingEntity, Double> transfer, LivingEntity living) {
        if (modUUIDs.containsKey(name) && modUUIDs.get(name) == uuid) return new DynamicAttributeModifier(uuid, name, operation, living, transfer);
        else {
            modUUIDs.put(name, Objects.requireNonNullElseGet(uuid, UUID::randomUUID));
        }
        return new DynamicAttributeModifier(modUUIDs.get(name), name, operation, living, transfer);
    }



    HashMap<String, UUID> modUUIDs = new HashMap<>();
    HashMap<EquipmentSlot, HashMap<String, UUID>> slotModUUIDs = new HashMap<>();

    /**
     * gets the UUID of a saved Name
     */
    static UUID getByName(String name) {
        return modUUIDs.get(name);
    }

    /**
     * simple Attribute modifier creation method
     */
    static AttributeModifier createModifier(String name, AttributeModifier.Operation operation, double amount) {
        if (!modUUIDs.containsKey(name)) {
            modUUIDs.put(name, UUID.randomUUID());
        }
        return new AttributeModifier(modUUIDs.get(name), name, amount, operation);
    }

    /**
     * simple Attribute modifier creation method for Armor Modifiers
     */
    static AttributeModifier createModifierForSlot(String name, AttributeModifier.Operation operation, double am, EquipmentSlot slot) {
        if (!slotModUUIDs.containsKey(slot)) {
            slotModUUIDs.put(slot, new HashMap<>());
        }
        HashMap<String, UUID> slotUUIDs = slotModUUIDs.get(slot);
        if (!slotUUIDs.containsKey(name)) {
            slotUUIDs.put(name, UUID.randomUUID());
        }
        return new AttributeModifier(slotUUIDs.get(name), name, am, operation);
    }

    /**
     * a class that makes merging attribute Modifiers easy
     */
    class AttributeBuilder {
        private Multimap<Attribute, AttributeModifier> modifiers;


        /**
         * simple creation method
         */
        public AttributeBuilder(Multimap<Attribute, AttributeModifier> multimap) {
            this.modifiers = multimap;
        }

        /**
         * merges the Multimap into the current
         */
        public void merge(Multimap<Attribute, AttributeModifier> toMerge) {
            this.modifiers = increaseAllByAmount(modifiers, toMerge);
        }

        /**
         * adds a simple instance
         */
        public void add(Attribute attribute, AttributeModifier modifier) {
            this.modifiers = increaseAllByAmount(modifiers, Map.of(attribute, modifier));
        }

        /**
         * method to use the map and possibly create a new one
        */
        public void update(UnaryOperator<Multimap<Attribute, AttributeModifier>> provider) {
            this.modifiers = provider.apply(this.modifiers);
        }

        /**
         * multiplies all entries
         */
        public void mulAll(double percent) {
            this.modifiers = increaseByPercent(this.modifiers, percent, AttributeModifier.Operation.values(), null);
        }

        /**
         * @param toMerge map of the value for each attribute to merge
         * @param operation the operation restriction
         */
        public void merge(HashMap<Attribute, Double> toMerge, AttributeModifier.Operation operation) {
            for (Attribute attribute : toMerge.keySet()) {
                this.modifiers = increaseByAmount(this.modifiers, attribute, new AttributeModifier("AttributeBuilderMerged", toMerge.get(attribute), operation));
            }
        }

        /**
         * the final method to turn the builder into a Multimap
         */
        public Multimap<Attribute, AttributeModifier> build() {
            return this.modifiers;
        }
    }
}