package net.kapitencraft.kap_lib.enchantment.event.handler;

import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBlockBreakEffect;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBowEffect;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentCountEffect;
import net.kapitencraft.kap_lib.enchantment.client.enchantment_color.ConfigureEnchantmentColorsCommand;
import net.kapitencraft.kap_lib.enchantment.extras.EnchantmentDescriptionManager;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.enchantment.ExtraEnchantmentEffectComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class Events {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player.level() instanceof ServerLevel level) {
            ItemStack tool = player.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!tool.isEmpty()) {
                MutableBoolean mutableBoolean = new MutableBoolean(false);
                BlockEntity blockEntity = level.getBlockEntity(event.getPos());
                BlockState state = event.getState();
                LootParams.Builder builder = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ATTACKING_ENTITY, player)
                        .withParameter(LootContextParams.BLOCK_STATE, state)
                        .withParameter(LootContextParams.TOOL, tool);
                if (blockEntity != null) builder.withParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
                EnchantmentHelper.runIterationOnItem(tool, (holder, i) -> {
                    builder.withParameter(LootContextParams.ENCHANTMENT_LEVEL, i);
                    LootParams params = builder.create(EnchantmentBlockBreakEffect.PARAM_SET);
                    LootContext context = new LootContext.Builder(params).create(Optional.empty());
                    for (ConditionalEffect<EnchantmentBlockBreakEffect> effect : holder.value().getEffects(ExtraEnchantmentEffectComponents.BLOCK_BREAK.get())) {
                        if (effect.matches(context)) mutableBoolean.setValue(effect.effect().onBreak(state, event.getPos(), level, i) || mutableBoolean.booleanValue());
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void tickArrows(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof AbstractArrow arrow) {
            CompoundTag arrowTag = arrow.getPersistentData();
            EnchantmentBowEffect.loadFromTag(null, arrowTag, EnchantmentBowEffect.ExePhase.TICK, 0, arrow);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void damageBonusRegister(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        if (event.getSource().getDirectEntity() instanceof Arrow arrow) {
            CompoundTag tag = arrow.getPersistentData();
            event.setNewDamage(EnchantmentBowEffect.loadFromTag(attacked, tag, EnchantmentBowEffect.ExePhase.HIT, event.getNewDamage(), arrow));
        }

        if (attacked.level() instanceof ServerLevel serverLevel) {
            DamageSource source = event.getSource();
            @Nullable LivingEntity attacker = MiscHelper.getAttacker(source);
            if (attacker == null) {
                return;
            }
            ItemStack stack = attacker.getMainHandItem();

            EnchantmentHelper.runIterationOnItem(stack, EquipmentSlot.MAINHAND, attacker, (enchantment, level, item) -> {
                LootContext context = Enchantment.damageContext(serverLevel, level, attacked, source);
                List<TargetedConditionalEffect<EnchantmentCountEffect>> effect = enchantment.value().getEffects(ExtraEnchantmentEffectComponents.COUNT.get());
                MutableFloat damage = new MutableFloat(event.getNewDamage());
                for (TargetedConditionalEffect<EnchantmentCountEffect> conditionalEffect : effect) {
                    if (conditionalEffect.matches(context))
                        damage.setValue(conditionalEffect.effect().tryExecute(enchantment, level, item, attacker, attacked, damage.floatValue(), source));
                }
                event.setNewDamage(damage.floatValue());
            });
        }
    }

    @SubscribeEvent
    public static void onClientPlayerNetworkLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        EnchantmentDescriptionManager.initApplication();
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ConfigureEnchantmentColorsCommand.register(event.getDispatcher());
    }
}