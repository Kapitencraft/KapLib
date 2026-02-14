package net.kapitencraft.kap_lib.bonus.event.handler;

import net.kapitencraft.kap_lib.bonus.requirement.BonusRequirementType;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.bonus.network.S2C.SyncBonusesPacket;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.kapitencraft.kap_lib.requirement.event.custom.RegisterRequirementTypesEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class BonusEvents {

    @SubscribeEvent
    public static void entityDeathEvents(LivingDeathEvent event) {
        LivingEntity toDie = event.getEntity();
        if (!event.isCanceled()) {
            BonusManager.deathEvent(toDie, event.getSource());
        }
    }

    @SubscribeEvent
    public static void addRequirementListener(AddReloadListenerEvent event) {
        event.addListener(BonusManager.updateInstance());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void miscDamageEvents(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        LivingEntity attacker = MiscHelper.getAttacker(event.getSource());
        BonusManager.attackEvent(attacked, attacker, event.getContainer());
    }

    @SubscribeEvent
    public static void playerLogIn(OnDatapackSyncEvent event) {
        event.getRelevantPlayers().forEach(p -> PacketDistributor.sendToPlayer(p,
                new SyncBonusesPacket(BonusManager.instance.createData())
        ));
    }
}
