package net.kapitencraft.kap_lib.event.custom.client;

import net.kapitencraft.kap_lib.item.modifier_display.EquipmentDisplayExtension;
import net.kapitencraft.kap_lib.item.modifier_display.WearableDisplayExtension;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;
import java.util.function.Function;

/**
 * register item modifier display extension

 * <p>This event is not cancellable, and does not have a result.</p>
 *
 * <p>This event is fired on the  mod-specific event bus,
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>

 * @apiNote registered modifiers will not automatically register to be used in the item modifiers.
 * meaning you have to implement that yourself
 */
//TODO add registering categories
public class RegisterItemModifiersDisplayExtensionsEvent extends Event implements IModBusEvent {
    private final List<Function<ItemStack, EquipmentDisplayExtension>> equipmentExtensionProviders;
    private final List<Function<ItemStack, WearableDisplayExtension>> wearableExtensionProviders;

    public RegisterItemModifiersDisplayExtensionsEvent(List<Function<ItemStack, EquipmentDisplayExtension>> equipmentExtensionProviders, List<Function<ItemStack, WearableDisplayExtension>> wearableExtensionProviders) {
        this.equipmentExtensionProviders = equipmentExtensionProviders;
        this.wearableExtensionProviders = wearableExtensionProviders;
    }

    public void registerEquipment(Function<ItemStack, EquipmentDisplayExtension> provider) {
        equipmentExtensionProviders.add(provider);
    }

    public void registerWearable(Function<ItemStack, WearableDisplayExtension> provider) {
        wearableExtensionProviders.add(provider);
    }
}
