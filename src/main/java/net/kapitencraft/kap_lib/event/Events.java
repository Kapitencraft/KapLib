package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.S2C.SyncRequirementsPacket;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.registry.ModAttributes;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.requirements.RequirementType;
import net.kapitencraft.kap_lib.util.DamageCounter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class Events {
    /**
     * event classes that should not be cancelled
     */
    private static final List<Class<? extends LivingEvent>> dontCancel = List.of(
            EntityItemPickupEvent.class,
            ItemTooltipEvent.class,
            RenderPlayerEvent.Pre.class,
            RenderPlayerEvent.Post.class,
            PlayerEvent.LoadFromFile.class,
            PlayerEvent.NameFormat.class,
            PlayerEvent.TabListNameFormat.class,
            PlayerEvent.PlayerLoggedInEvent.class,
            PlayerEvent.PlayerLoggedOutEvent.class,
            MovementInputUpdateEvent.class,
            LivingMakeBrainEvent.class,
            LivingEvent.LivingTickEvent.class,
            LivingBreatheEvent.class
    );

    @SubscribeEvent
    public static void ensureReqsMet(LivingEvent event) { //cancel any PlayerEvent that don't meet the item requirements
        if (!dontCancel.contains(event.getClass()) && !RequirementManager.meetsRequirementsFromEvent(event, EquipmentSlot.MAINHAND) && event.isCancelable()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void addRequirementListener(AddReloadListenerEvent event) {
        event.addListener(RequirementManager.instance = new RequirementManager());
        event.addListener(BonusManager.updateInstance(event.getRegistryAccess()));
    }

    @SubscribeEvent
    public static void playerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToClientPlayer(new SyncRequirementsPacket(RequirementManager.instance), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void addReqDisplay(ItemTooltipEvent event) {
        ClientHelper.addReqContent(event.getToolTip()::add, RequirementType.ITEM, event.getItemStack().getItem(), event.getEntity());
    }

    @SubscribeEvent
    public static void healingDisplay(LivingHealEvent event) {
        if (event.getAmount() > 0) MiscHelper.createDamageIndicator(event.getEntity(), event.getAmount(), "heal");

    }

    @SubscribeEvent
    public static void damageTick(LivingDamageEvent event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        boolean dodge = false;
        double dodgePercentage = AttributeHelper.getSaveAttributeValue(ModAttributes.DODGE.get(), attacked);
        if (dodgePercentage > 0) {
            if (MathHelper.chance(dodgePercentage / 100, attacked) && !(source.is(DamageTypeTags.BYPASSES_ARMOR) || source.is(DamageTypeTags.IS_FALL) || source.is(DamageTypeTags.IS_FIRE))) {
                dodge = true;
                event.setAmount(0);
            }
        }
        MiscHelper.createDamageIndicator(attacked, event.getAmount(), dodge ? "dodge" : source.getMsgId());
        DamageCounter.increaseDamage(event.getAmount());
    }
}
