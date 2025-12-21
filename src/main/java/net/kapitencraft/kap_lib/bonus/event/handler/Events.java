package net.kapitencraft.kap_lib.bonus.event.handler;

import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.bonus.network.S2C.SyncBonusesPacket;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class Events {

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
        event.setNewDamage(BonusManager.attackEvent(attacked, attacker, MiscHelper.getDamageType(event.getSource()), event.getNewDamage()));
    }

    @SubscribeEvent
    public static void playerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new SyncBonusesPacket(BonusManager.instance.createData())
            );
        }
    }
}
