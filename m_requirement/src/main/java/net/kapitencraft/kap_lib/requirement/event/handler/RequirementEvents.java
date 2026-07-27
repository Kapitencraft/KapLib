package net.kapitencraft.kap_lib.requirement.event.handler;

import net.kapitencraft.kap_lib.requirement.network.S2C.SyncRequirementsPacket;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.type.RequirementType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

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
    public static void onBlockEntityPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof LivingEntity living)
            event.setCanceled(!RequirementManager.meetsBlockRequirementsFromEvent(event, living));
    }

    @SubscribeEvent
    public static void onBlockEntityMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getEntity() instanceof LivingEntity living)
            event.setCanceled(!RequirementManager.meetsBlockRequirementsFromEvent(event, living));
    }

    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        event.setCanceled(!RequirementManager.meetsBlockRequirements(state.getBlock(), event.getEntity()));
    }

    @SubscribeEvent
    public static void addReqDisplay(ItemTooltipEvent event) {
        List<Component> list = event.getToolTip();
        if (event.getItemStack().getItem() instanceof BlockItem blockItem) {
            RequirementManager.addReqContent(list::add, RequirementType.BLOCK, blockItem.getBlock(), event.getEntity());
        } else {
            RequirementManager.addReqContent(list::add, RequirementType.ITEM, event.getItemStack().getItem(), event.getEntity());
        }
    }
}
