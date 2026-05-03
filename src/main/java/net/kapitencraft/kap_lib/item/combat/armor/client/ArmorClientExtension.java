package net.kapitencraft.kap_lib.item.combat.armor.client;

import net.kapitencraft.kap_lib.item.combat.armor.AbstractArmorItem;
import net.kapitencraft.kap_lib.item.combat.armor.client.provider.ArmorModelProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Client extension wrapper to add armor models
 */
public class ArmorClientExtension implements IClientItemExtensions {

    private final ArmorModelProvider provider;

    /**
     * creates a new Armor Client Extension
     * @param provider the model provider
     */
    public ArmorClientExtension(ArmorModelProvider provider) {
        this.provider = provider;
    }

    @Override
    public @NotNull ArmorModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
        ArmorModel armorModel = provider.getModel(living, stack, slot);
        armorModel.partVisible(slot);
        armorModel.crouching = original.crouching;
        armorModel.riding = original.riding;
        armorModel.young = original.young;
        return armorModel;
    }

    @Override
    public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        //fixes visibility bug because forge pain
        ArmorModel model = getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);
        copyModelProperties(original, model);
        return model;
    }

    @ApiStatus.Internal
    private <T extends LivingEntity> void copyModelProperties(HumanoidModel<T> original, ArmorModel replacement) {
        original.copyPropertiesTo((HumanoidModel<T>) replacement);
        replacement.rightBoot.copyFrom(original.rightLeg);
        replacement.leftBoot.copyFrom(original.leftLeg);
    }

    /**
     * used to register custom armor extensions
     * @param <T> the class type of the armor item
     * @param map the armor to register
     * @param event the event to register to
     * @param provider the model to use
     */
    public static <T extends AbstractArmorItem> void register(Map<ArmorItem.Type, DeferredItem<T>> map, RegisterClientExtensionsEvent event, ArmorModelProvider provider) {
        event.registerItem(new ArmorClientExtension(provider), map.values().toArray(DeferredItem[]::new));
    }
}
