package net.kapitencraft.kap_lib.two_handed;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.event.custom.client.RegisterOrderedItemComponentTooltipEvent;
import net.kapitencraft.kap_lib.two_handed.registry.THItemComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingSwapItemsEvent;

@Mod(TwoHandedModule.MODULE_ID)
public class TwoHandedModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_two_handed";
    public static final int OFFHAND_SLOT_ID = 40;

    public TwoHandedModule(IEventBus modEventBus) {
        THItemComponents.REGISTRY.register(modEventBus);

        modEventBus.addListener(TwoHandedModule::onRegisterOrderedItemComponentTooltip);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, TwoHandedModule::onLivingSwapItems);
    }

    public static void onRegisterOrderedItemComponentTooltip(RegisterOrderedItemComponentTooltipEvent event) {
        event.insertBefore(THItemComponents.TWO_HANDED, DataComponents.UNBREAKABLE);
    }

    public static boolean isTwoHanded(ItemStack stack) {
        return stack.has(THItemComponents.TWO_HANDED);
    }

    public static void onLivingSwapItems(LivingSwapItemsEvent.Hands event) {
        if (TwoHandedModule.isTwoHanded(event.getItemSwappedToMainHand())) {
            event.setCanceled(true);
        }
    }
}
