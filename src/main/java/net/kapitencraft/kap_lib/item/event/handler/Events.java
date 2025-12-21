package net.kapitencraft.kap_lib.item.event.handler;

import net.kapitencraft.kap_lib.core.helpers.InventoryHelper;
import net.kapitencraft.kap_lib.core.network.S2C.DisplayTotemActivationPacket;
import net.kapitencraft.kap_lib.item.combat.totem.AbstractTotemItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

@EventBusSubscriber
public class Events {

    @SubscribeEvent
    public static void entityDeathEvents(LivingDeathEvent event) {
        LivingEntity toDie = event.getEntity();
        if (toDie instanceof ServerPlayer player) {
            Collection<ItemStack> totems = InventoryHelper.getByFilter(player, stack -> stack.getItem() instanceof AbstractTotemItem);
            if (!event.isCanceled()) for (ItemStack stack : totems) {
                AbstractTotemItem totemItem = (AbstractTotemItem) stack.getItem();
                if (totemItem.onUse(player, event.getSource())) {
                    player.awardStat(Stats.ITEM_USED.get(totemItem));
                    event.setCanceled(true);
                    PacketDistributor.sendToPlayer(player, new DisplayTotemActivationPacket(stack.copy(), player.getId()));
                    stack.shrink(1);
                    break;
                }
            }
        }
    }
}
