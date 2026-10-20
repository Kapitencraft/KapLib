package net.kapitencraft.kap_lib.two_handed;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.event.custom.client.RegisterOrderedItemComponentTooltipEvent;
import net.kapitencraft.kap_lib.two_handed.registry.THItemComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingSwapItemsEvent;
import org.jetbrains.annotations.ApiStatus;

@Mod(TwoHandedModule.MODULE_ID)
public class TwoHandedModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_two_handed";
    public static final int OFFHAND_SLOT_ID = 40;

    public TwoHandedModule(IEventBus modEventBus) {
        THItemComponents.REGISTRY.register(modEventBus);

        modEventBus.addListener(TwoHandedModule::onRegisterOrderedItemComponentTooltip);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, TwoHandedModule::onLivingSwapItems);
        NeoForge.EVENT_BUS.addListener(TwoHandedModule::registerServerCommand);

    }

    @ApiStatus.Internal
    static void registerServerCommand(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        TwoHandedServerTestCommand.register(dispatcher);
    }

    public static void onRegisterOrderedItemComponentTooltip(RegisterOrderedItemComponentTooltipEvent event) {
        event.insertBefore(THItemComponents.TWO_HANDED, DataComponents.UNBREAKABLE);
    }

    public static boolean isItemTwoHanded(ItemStack stack) {
        return stack.has(THItemComponents.TWO_HANDED);
    }

    public static boolean isTwoHanded(ItemStack stack, LivingEntity living) {
         return isItemTwoHanded(stack) && !living.kap_lib$suppressesTwoHanded();
    }

    public static void onLivingSwapItems(LivingSwapItemsEvent.Hands event) {
        if (TwoHandedModule.isTwoHanded(event.getItemSwappedToMainHand(), event.getEntity())) {
            event.setCanceled(true);
        }
    }
}
