package net.kapitencraft.kap_lib.event.custom.client;

import net.kapitencraft.kap_lib.item.modifier_display.ItemModifiersDisplayExtension;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.function.BiFunction;

/**
 * register item modifier display extension

 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>

 * @apiNote registered modifiers will not automatically register to be used in the item modifiers.
 * meaning you have to implement that yourself
 */
public class RegisterItemModifiersDisplayExtensionsEvent extends Event implements IModBusEvent {
    private final List<BiFunction<LivingEntity, ItemStack, ItemModifiersDisplayExtension>> extensionProviders;

    public RegisterItemModifiersDisplayExtensionsEvent(List<BiFunction<LivingEntity, ItemStack, ItemModifiersDisplayExtension>> extensionProviders) {
        this.extensionProviders = extensionProviders;
    }

    public void register(BiFunction<LivingEntity, ItemStack, ItemModifiersDisplayExtension> provider) {
        extensionProviders.add(provider);
    }
}
