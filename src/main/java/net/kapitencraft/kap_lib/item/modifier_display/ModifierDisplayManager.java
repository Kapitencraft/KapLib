package net.kapitencraft.kap_lib.item.modifier_display;

import net.kapitencraft.kap_lib.event.custom.client.RegisterItemModifiersDisplayExtensionsEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ModifierDisplayManager {
    private static final List<Function<ItemStack, @Nullable EquipmentDisplayExtension>> equipmentProviders = new ArrayList<>();
    private static final List<Function<ItemStack, @Nullable WearableDisplayExtension>> wearableProviders = new ArrayList<>();

    public static void init() {
        var event = new RegisterItemModifiersDisplayExtensionsEvent(equipmentProviders, wearableProviders);
        ModLoader.postEvent(event);
    }

    public static ExtensionData getExtensions(ItemStack obj) {
        List<EquipmentDisplayExtension> equipment = new ArrayList<>();
        for (Function<ItemStack, EquipmentDisplayExtension> provider : equipmentProviders) {
            Optional.ofNullable(provider.apply(obj)).ifPresent(equipment::add);
        }

        List<WearableDisplayExtension> wearable = new ArrayList<>();
        for (Function<ItemStack, WearableDisplayExtension> provider : wearableProviders) {
            Optional.ofNullable(provider.apply(obj)).ifPresent(wearable::add);
        }

        return new ExtensionData(equipment, wearable);
    }

    public record ExtensionData(List<EquipmentDisplayExtension> equipmentProviders,
                                List<WearableDisplayExtension> wearableProviders) {

    }
}
