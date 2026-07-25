package net.kapitencraft.kap_lib.two_handed;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.event.custom.client.RegisterOrderedItemComponentTooltipEvent;
import net.kapitencraft.kap_lib.two_handed.registry.THItemComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(TwoHandedModule.MODULE_ID)
public class TwoHandedModule {
    public static final String MODULE_ID = LibConstants.MOD_ID + "_two_handed";
    public static final int OFFHAND_SLOT_ID = 40;

    public TwoHandedModule(IEventBus modEventBus, ModContainer container) {
        THItemComponents.REGISTRY.register(modEventBus);

        modEventBus.addListener(TwoHandedModule::onRegisterOrderedItemComponentTooltip);
        NeoForge.EVENT_BUS.addListener(TwoHandedModule::onLivingEquipmentChange);
    }

    public static void onRegisterOrderedItemComponentTooltip(RegisterOrderedItemComponentTooltipEvent event) {
        event.insertBefore(THItemComponents.TWO_HANDED, DataComponents.UNBREAKABLE);
    }

    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getSlot() == EquipmentSlot.MAINHAND) {
                if (event.getTo().has(THItemComponents.TWO_HANDED) && !event.getFrom().has(THItemComponents.TWO_HANDED)) {
                    player.getInventory().placeItemBackInInventory(player.getOffhandItem());
                    player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                }
            }
        }
    }
}
