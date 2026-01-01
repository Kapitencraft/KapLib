package net.kapitencraft.kap_lib.item.modifier_display;

import net.kapitencraft.kap_lib.item.event.custom.client.RegisterItemModifiersDisplayExtensionsEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.gametest.GameTestHooks;
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
        if (GameTestHooks.isGametestEnabled()) {
            equipmentProviders.add(s -> s.is(Items.NETHERITE_SWORD) ? new EquipmentDisplayExtension() {
                @Override
                public ResourceLocation getModifiersLocation() {
                    return Item.BASE_ATTACK_DAMAGE_ID;
                }

                @Override
                public Style getStyle() {
                    return Style.EMPTY.withColor(ChatFormatting.GOLD);
                }

                @Override
                public Type getType() {
                    return Type.CURLY;
                }
            } : null);
            equipmentProviders.add(s -> s.is(ItemTags.SWORDS) ? new EquipmentDisplayExtension() {
                @Override
                public ResourceLocation getModifiersLocation() {
                    return Item.BASE_ATTACK_DAMAGE_ID;
                }

                @Override
                public Style getStyle() {
                    return Style.EMPTY.withColor(ChatFormatting.AQUA);
                }

                @Override
                public Type getType() {
                    return Type.POINTY;
                }
            } : null);
        }
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
