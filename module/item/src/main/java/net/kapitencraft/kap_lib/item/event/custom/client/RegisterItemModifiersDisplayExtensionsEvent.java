package net.kapitencraft.kap_lib.item.event.custom.client;

import net.kapitencraft.kap_lib.item.modifier_display.EquipmentDisplayExtension;
import net.kapitencraft.kap_lib.item.modifier_display.WearableDisplayExtension;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * register item modifier display extension
 *
 * <p>This event is not cancellable, and does not have a result.</p>
 *
 * <p>This event is fired on the  mod-specific event bus,
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 *
 * //@apiNote registered modifiers will not automatically register to be used in the item modifiers.
 * meaning you have to implement that yourself
 */
public class RegisterItemModifiersDisplayExtensionsEvent extends Event implements IModBusEvent {
    private final List<Function<ItemStack, @Nullable EquipmentDisplayExtension>> equipmentExtensionProviders;
    private final List<Function<ItemStack, @Nullable WearableDisplayExtension>> wearableExtensionProviders;

    public RegisterItemModifiersDisplayExtensionsEvent(List<Function<ItemStack, EquipmentDisplayExtension>> equipmentExtensionProviders, List<Function<ItemStack, WearableDisplayExtension>> wearableExtensionProviders) {
        this.equipmentExtensionProviders = equipmentExtensionProviders;
        this.wearableExtensionProviders = wearableExtensionProviders;
    }

    public void registerEquipment(Function<ItemStack, @Nullable EquipmentDisplayExtension> provider) {
        equipmentExtensionProviders.add(provider);
    }

    public void registerWearable(Function<ItemStack, @Nullable WearableDisplayExtension> provider) {
        wearableExtensionProviders.add(provider);
    }
}
