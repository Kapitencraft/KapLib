package net.kapitencraft.kap_lib.requirement.event.handler;

import net.kapitencraft.kap_lib.requirement.network.S2C.SyncRequirementsPacket;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.type.RegistryReqType;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class RequirementEvents {

    @SubscribeEvent
    public static void addRequirementListener(AddReloadListenerEvent event) {
        event.addListener(RequirementManager.instance = new RequirementManager());
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        event.setCanceled(!RequirementManager.meetsItemRequirementsFromEvent(event, EquipmentSlot.MAINHAND));
    }

    @SubscribeEvent
    public static void syncRequirements(OnDatapackSyncEvent event) {
        event.getRelevantPlayers().forEach(p -> PacketDistributor.sendToPlayer(p,
                SyncRequirementsPacket.create()
        ));
    }

    @SubscribeEvent
    public static void addReqDisplay(ItemTooltipEvent event) {
        RequirementManager.addReqContent(event.getToolTip()::add, RegistryReqType.ITEM, event.getItemStack().getItem(), event.getEntity());
    }

}
