package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.kapitencraft.kap_lib.attribute.TimedModifiers;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.kapitencraft.kap_lib.bonus.network.S2C.SyncBonusesPacket;
import net.kapitencraft.kap_lib.bonus.requirement.BonusRequirementType;
import net.kapitencraft.kap_lib.component.ExtraComponents;
import net.kapitencraft.kap_lib.cooldown.CooldownAttributeAdder;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.kapitencraft.kap_lib.core.helpers.ParticleHelper;
import net.kapitencraft.kap_lib.core.tags.ExtraTags;
import net.kapitencraft.kap_lib.enchantment.ExtraEnchantmentEffectComponents;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBlockBreakEffect;
import net.kapitencraft.kap_lib.enchantment.abstracts.EnchantmentBowEffect;
import net.kapitencraft.kap_lib.enchantment.extras.EnchantmentDescriptionManager;
import net.kapitencraft.kap_lib.inventory_page.wearable.Wearables;
import net.kapitencraft.kap_lib.mana.ManaAttachmentTypes;
import net.kapitencraft.kap_lib.mana.ManaAttributes;
import net.kapitencraft.kap_lib.particle.custom.DamageIndicatorParticleOptions;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.requirement.event.custom.RegisterRequirementTypesEvent;
import net.kapitencraft.kap_lib.requirement.network.S2C.SyncRequirementsPacket;
import net.kapitencraft.kap_lib.requirement.type.RegistryReqType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

/**
 * event listeners for KapLib.
 * <br>there shouldn't be any reason for Modders to use this class
 */
@ApiStatus.Internal
@EventBusSubscriber
public class Events {

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ManaAttributes.MANA_COST);
        event.add(EntityType.PLAYER, ManaAttributes.MANA_REGEN);
        event.add(EntityType.PLAYER, ManaAttributes.MAX_MANA);
        CooldownAttributeAdder.addAttributes(event);
    }

    @SubscribeEvent
    public static void addRequirementListener(AddReloadListenerEvent event) {
        event.addListener(RequirementManager.instance);
        event.addListener(BonusManager.updateInstance());
    }

    @SubscribeEvent
    public static void appendPlayerHead(PlayerEvent.NameFormat event) {
        event.setDisplayname(ExtraComponents.playerHead(event.getEntity().getUUID()).append(event.getDisplayname()));
    }

    @SubscribeEvent
    public static void playerLogIn(OnDatapackSyncEvent event) {
        event.getRelevantPlayers().forEach(p -> PacketDistributor.sendToPlayer(p,
                SyncRequirementsPacket.create(),
                new SyncBonusesPacket(BonusManager.instance.createData())
        ));
    }

    @SubscribeEvent
    public static void addReqDisplay(ItemTooltipEvent event) {
        RequirementManager.addReqContent(event.getToolTip()::add, RegistryReqType.ITEM, event.getItemStack().getItem(), event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void healingDisplay(LivingHealEvent event) {
        if (event.getAmount() > 0) DamageIndicatorParticleOptions.create(event.getEntity(), event.getAmount(), "heal");
    }

    @SubscribeEvent
    public static void modArrowEnchantments(ArrowLooseEvent event) {
        event.setCharge((int) (event.getCharge() * event.getEntity().getAttributeValue(ExtraAttributes.DRAW_SPEED) / 100));
    }

    @SubscribeEvent
    public static void joinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.level() instanceof ServerLevel serverLevel) {
            if (arrow.getOwner() instanceof LivingEntity living) {
                ItemStack bow = living.getUseItem();
                CompoundTag arrowTag = arrow.getPersistentData();
                if (bow.is(ExtraTags.Items.HITS_ENDERMAN)) {
                    arrowTag.putBoolean("HitsEnderMan", true);
                }
                EnchantedItemInUse itemInUse = new EnchantedItemInUse(bow, living.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, living);

                EnchantmentHelper.runIterationOnItem(bow, (enchantment, level) -> {
                    LootContext context = Enchantment.entityContext(serverLevel, level, arrow, arrow.position());
                    enchantment.value().getEffects(ExtraEnchantmentEffectComponents.BOW_SPAWN.value()).forEach(enchantmentEntityEffect -> {
                        if (enchantmentEntityEffect.matches(context)) {
                            enchantmentEntityEffect.effect().apply(serverLevel, level, itemInUse, arrow, arrow.position());
                        }
                    });
                    ListTag list = IOHelper.getOrCreateList(arrow.getPersistentData(), enchantment.getKey().location().toString(), Tag.TAG_COMPOUND);
                    List<ConditionalEffect<EnchantmentBowEffect>> effects = enchantment.value().getEffects(ExtraEnchantmentEffectComponents.BOW.value());
                    for (int i = 0; i < effects.size(); i++) {
                        CompoundTag tag = new CompoundTag();
                        ConditionalEffect<EnchantmentBowEffect> effect = effects.get(i);
                        if (effect.matches(context))
                            effect.effect().write(tag, level, bow, living, arrow);
                        list.add(i, tag);
                    }
                });
            }
        }
        if (event.getEntity() instanceof Player player) {
            CompoundTag tag = player.getPersistentData();
            double mana; //upload lost mana
            if (tag.contains("Mana", Tag.TAG_DOUBLE)) {
                mana = tag.getDouble("Mana");
            } else mana = 100;
            player.setData(ManaAttachmentTypes.MANA, mana);
            if (tag.contains("Health", Tag.TAG_FLOAT)) {
                player.setHealth(tag.getFloat("Health"));
            }
        }
    }

    @SubscribeEvent
    public static void onClientPlayerNetworkLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        EnchantmentDescriptionManager.initApplication();
    }

    @SubscribeEvent
    public static void onClientPlayerNetworkLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        EnchantmentDescriptionManager.reset();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer sP = (ServerPlayer) event.getEntity();
        Wearables.send(sP);
        Cooldowns.send(sP);
    }

    @SubscribeEvent
    public static void leaveLevelEvent(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            //save mana to reset back to when re-joining
            player.getPersistentData().putDouble("Mana", player.getData(ManaAttachmentTypes.MANA));
        }
        if (event.getEntity().level().isClientSide()) {
            TerminatorTriggers.ENTITY_REMOVED.get().trigger(event.getEntity().getId());
        }
    }

    @SubscribeEvent
    public static void tickArrows(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof AbstractArrow arrow) {
            CompoundTag arrowTag = arrow.getPersistentData();
            EnchantmentBowEffect.loadFromTag(null, arrowTag, EnchantmentBowEffect.ExePhase.TICK, 0, arrow);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void healthRegenRegister(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (living.getAttribute(ExtraAttributes.VITALITY) != null) {
            double vitality = living.getAttributeValue(ExtraAttributes.VITALITY);
            event.setAmount(event.getAmount() * (1 + (float) vitality / 100));
        }
    }

    public static final String DOUBLE_JUMP_ID = "currentDoubleJump";

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        CompoundTag tag = player.getPersistentData();
        if (!player.onGround()) {
            if (canJump(player) && tag.getInt(DOUBLE_JUMP_ID) < player.getAttributeValue(ExtraAttributes.DOUBLE_JUMP)) {
                if (player.jumping && player.noJumpDelay <= 0) {
                    ParticleHelper.sendAlwaysVisibleParticles(ParticleTypes.CLOUD, player.level(), player.getX(), player.getY(), player.getZ(), 0.25, 0.0, 0.25, 0, 0, 0, 15);
                    player.noJumpDelay = 10;
                    player.fallDistance = 0;
                    Vec3 targetLoc = player.getLookAngle().multiply(1, 0, 1).scale(0.75).add(0, 1, 0);
                    player.setDeltaMovement(targetLoc.x, targetLoc.y > 0 ? targetLoc.y : -targetLoc.y, targetLoc.z);
                    player.hurtMarked = true;
                    IOHelper.increaseIntegerTagValue(player.getPersistentData(), DOUBLE_JUMP_ID, 1);
                }
            }
        } else if (tag.getInt(DOUBLE_JUMP_ID) > 0) {
            tag.putInt(DOUBLE_JUMP_ID, 0);
        }
        TimedModifiers.get(player).tick(player);
        Cooldowns.get(player).tick(player);
    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living) || living.isDeadOrDying()) return;
        if (living instanceof Mob mob) {
            if (mob.getTarget() != null && mob.getTarget().isInvisible()) {
                mob.setTarget(null);
            }
        }
    }

    private static boolean canJump(Player player) {
        return !player.onGround() && !(player.isPassenger() || player.getAbilities().flying) && !(player.isInWater() || player.isInLava());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDrops(BlockDropsEvent event) {
        if (event.getBreaker() instanceof Player player) {
            double scale = ExtraAttributes.getExperienceScale(player);
            event.setDroppedExperience((int) (event.getDroppedExperience() * scale));
        }
    }

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
                        if (effect.matches(context))
                            mutableBoolean.setValue(effect.effect().onBreak(state, event.getPos(), level, i) || mutableBoolean.booleanValue());
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null) {
            event.setDroppedExperience((int) (event.getDroppedExperience() * ExtraAttributes.getExperienceScale(player)));
        }
    }

    @SubscribeEvent
    public static void onRegisterRequirementTypes(RegisterRequirementTypesEvent event) {
        event.add(BonusRequirementType.INSTANCE);
    }
}
