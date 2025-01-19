package net.kapitencraft.kap_lib.mixin.classes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends Item {

    @Shadow
    @Final
    private Multimap<Attribute, AttributeModifier> defaultModifiers;

    public DiggerItemMixin(Item.Properties p_41383_) {
        super(p_41383_);
    }

    @Accessor
    abstract float getSpeed();


    /**
     * @author Kapitencraft
     * @reason Mining Speed Modifier
     */
    @Overwrite
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            HashMultimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
            multimap.putAll(this.defaultModifiers);
            multimap.put(ExtraAttributes.MINING_SPEED.get(), AttributeHelper.createModifier("Digger Item Speed Modifier", AttributeModifier.Operation.ADDITION, getSpeed()));
            return multimap;
        }
        return super.getDefaultAttributeModifiers(slot);
    }
}
