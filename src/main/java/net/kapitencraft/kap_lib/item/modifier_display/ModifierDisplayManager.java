package net.kapitencraft.kap_lib.item.modifier_display;

import net.kapitencraft.kap_lib.event.ModEventFactory;
import net.kapitencraft.kap_lib.event.custom.client.RegisterItemModifiersDisplayExtensionsEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class ModifierDisplayManager {
    private static final List<BiFunction<LivingEntity, ItemStack, ItemModifiersDisplayExtension>> providers = new ArrayList<>();

    public static void init() {
        var event = new RegisterItemModifiersDisplayExtensionsEvent(providers);
        ModEventFactory.fireModEvent(event);
    }

    public static List<ItemModifiersDisplayExtension> getExtensions(LivingEntity owner, ItemStack obj) {
        List<ItemModifiersDisplayExtension> list = new ArrayList<>();
        for (BiFunction<LivingEntity, ItemStack, ItemModifiersDisplayExtension> provider : providers) {
            Optional.ofNullable(provider.apply(owner, obj)).ifPresent(list::add);
        }
        return list;
    }
}
