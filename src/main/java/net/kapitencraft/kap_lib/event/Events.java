package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.client.ExtraComponents;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadAllocator;
import net.kapitencraft.kap_lib.collection.Queue;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.enchantments.abstracts.ModBowEnchantment;
import net.kapitencraft.kap_lib.helpers.*;
import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.kapitencraft.kap_lib.io.network.S2C.SyncBonusesPacket;
import net.kapitencraft.kap_lib.io.network.S2C.SyncRequirementsPacket;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.custom.particle_animation.TerminatorTriggers;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.kapitencraft.kap_lib.requirements.type.RequirementType;
import net.kapitencraft.kap_lib.spawn_table.SpawnTableManager;
import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * event listeners for KapLib.
 * <br>there shouldn't be any reason for Modders to use this class
 */
@ApiStatus.Internal
@EventBusSubscriber
public class Events {
    /**
     * event classes that should not be cancelled
     */
    private static final List<Class<? extends LivingEvent>> dontCancel = List.of(
            ItemTooltipEvent.class,
            RenderPlayerEvent.Pre.class,
            RenderPlayerEvent.Post.class,
            PlayerEvent.LoadFromFile.class,
            PlayerEvent.NameFormat.class,
            PlayerEvent.TabListNameFormat.class,
            PlayerEvent.PlayerLoggedInEvent.class,
            PlayerEvent.PlayerLoggedOutEvent.class,
            MovementInputUpdateEvent.class,
            LivingBreatheEvent.class
    );

    @SubscribeEvent
    public static void ensureReqsMet(LivingEvent event) { //cancel any PlayerEvent that don't meet the item requirements
        if (event instanceof ICancellableEvent iCE && !dontCancel.contains(event.getClass()) && !RequirementManager.meetsItemRequirementsFromEvent(event, EquipmentSlot.MAINHAND) && iCE.isCanceled()) iCE.setCanceled(true);
    }

    @SubscribeEvent
    public static void addRequirementListener(AddReloadListenerEvent event) {
        event.addListener(RequirementManager.instance);
        event.addListener(BonusManager.updateInstance());
        event.addListener(SpawnTableManager.instance = new SpawnTableManager());
    }

    @SubscribeEvent
    public static void appendPlayerHead(PlayerEvent.NameFormat event) {
        event.setDisplayname(ExtraComponents.playerHead(event.getEntity().getUUID()).append(event.getDisplayname()));
    }


    @SubscribeEvent
    public static void playerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new SyncRequirementsPacket(RequirementManager.createData()),
                    new SyncBonusesPacket(BonusManager.instance.createData())
            );
        }
    }

    @SubscribeEvent
    public static void addReqDisplay(ItemTooltipEvent event) {
        ClientHelper.addReqContent(event.getToolTip()::add, RegistryReqType.ITEM, event.getItemStack().getItem(), event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void healingDisplay(LivingHealEvent event) {
        if (event.getAmount() > 0) MiscHelper.createDamageIndicator(event.getEntity(), event.getAmount(), "heal");
    }

    @SubscribeEvent
    public static void modArrowEnchantments(ArrowLooseEvent event) {
        event.setCharge((int) (event.getCharge() * event.getEntity().getAttributeValue(ExtraAttributes.DRAW_SPEED) / 100));
    }

    private static final Map<ResourceKey<Level>, Queue<UUID>> arrowHelper = new HashMap<>();

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            Level level = (Level) event.getLevel();
            arrowHelper.remove(level.dimension()); //clear arrow holder when dimension gets unloaded
        }
    }

    @SubscribeEvent
    public static void joinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && !arrow.level().isClientSide()) {
            if (arrow.getOwner() instanceof LivingEntity living) {
                ItemStack bow = living.getMainHandItem();
                CompoundTag arrowTag = arrow.getPersistentData();
                if (bow.is(ExtraTags.Items.HITS_ENDERMAN)) {
                    arrowTag.putBoolean("HitsEnderMan", true);
                }
                for (Enchantment enchantment : bow.getAllEnchantments().keySet()) {
                    if (enchantment instanceof ModBowEnchantment bowEnchantment && RequirementManager.instance.meetsRequirements(RequirementType.ENCHANTMENT, enchantment, living)) {
                        CompoundTag tag = new CompoundTag();
                        int level = bow.getEnchantmentLevel(enchantment);
                        tag.putInt("Level", level);
                        arrowTag.put(Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(enchantment), "unknown enchantment: " + enchantment).toString(), bowEnchantment.write(tag, level, bow, living, arrow));
                        if (bowEnchantment.shouldTick()) arrowHelper.get(arrow.level().dimension()).add(arrow.getUUID());
                    }
                }
            }
        }
        if (event.getEntity() instanceof Player player) {
            AttributeInstance manaInst = player.getAttribute(ExtraAttributes.MANA);
            CompoundTag tag = player.getPersistentData();
            if (manaInst == null) throw new IllegalStateException();
            else {
                double mana; //upload lost mana
                if (tag.contains("Mana", Tag.TAG_DOUBLE)) {
                    mana = tag.getDouble("Mana");
                } else mana = 100;
                manaInst.setBaseValue(mana);
            }
            if (tag.contains("Health", Tag.TAG_FLOAT)) {
                player.setHealth(tag.getFloat("Health"));
            }

            if (player instanceof ServerPlayer sP) {
                Wearables.send(sP);
                Cooldowns.send(sP);
            }
        }
    }

    @SubscribeEvent
    public static void leaveLevelEvent(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            //save mana to reset back to when re-joining
            player.getPersistentData().putDouble("Mana", player.getAttributeValue(ExtraAttributes.MANA));
        }
        if (event.getEntity().level().isClientSide()) {
            TerminatorTriggers.ENTITY_REMOVED.get().trigger(event.getEntity().getId());
        }
    }

    @SubscribeEvent
    public static void serverTick(LevelTickEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            arrowHelper.putIfAbsent(serverLevel.dimension(), Queue.create());
            Queue<UUID> queue = arrowHelper.get(serverLevel.dimension());
            queue.queue(uuid -> {
                Arrow arrow = (Arrow) serverLevel.getEntity(uuid);
                if (arrow != null) {
                    CompoundTag arrowTag = arrow.getPersistentData();
                    ModBowEnchantment.loadFromTag(null, arrowTag, ModBowEnchantment.ExePhase.TICK, 0, arrow);
                } else {
                    queue.remove(uuid);
                }
            });
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

    @SubscribeEvent
    public static void onGameShuttingDown(GameShuttingDownEvent event) {
        PlayerHeadAllocator.getInstance().shutDown();
    }


    public static final String DOUBLE_JUMP_ID = "currentDoubleJump";

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        Player player = event.getEntity();
        CompoundTag tag = player.getPersistentData();
        if (!player.onGround()) {
            if (canJump(player) && tag.getInt(DOUBLE_JUMP_ID) < player.getAttributeValue(ExtraAttributes.DOUBLE_JUMP)) {
                if (player.jumping && player.noJumpDelay <= 0) {
                    ParticleHelper.sendAlwaysVisibleParticles(ParticleTypes.CLOUD, player.level(), player.getX(), player.getY(), player.getZ(), 0.25, 0.0, 0.25, 0,0,0, 15);
                    player.noJumpDelay = 10; player.fallDistance = 0;
                    Vec3 targetLoc = player.getLookAngle().multiply(1, 0, 1).scale(0.75).add(0, 1, 0);
                    player.setDeltaMovement(targetLoc.x, targetLoc.y > 0 ? targetLoc.y : -targetLoc.y, targetLoc.z);
                    IOHelper.increaseIntegerTagValue(player.getPersistentData(), DOUBLE_JUMP_ID, 1);
                }
            }
        } else if (tag.getInt(DOUBLE_JUMP_ID) > 0) {
            tag.putInt(DOUBLE_JUMP_ID, 0);
        }
    }


    @SubscribeEvent
    public static void entityTick(EntityTickEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living) || living.isDeadOrDying()) return;
        Cooldowns.get(living).tick();
        BonusHelper.tickEnchantments(living);
        CompoundTag tag = living.getPersistentData();
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
    public void onBlockBreak(BlockDropsEvent event) {
        if (event.getBreaker() instanceof Player player) {
            double scale = AttributeHelper.getExperienceScale(player);
            event.setDroppedExperience((int) (event.getDroppedExperience() * scale));
        }
    }

    @SubscribeEvent
    public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null) {
            event.setDroppedExperience((int) (event.getDroppedExperience() * AttributeHelper.getExperienceScale(player)));
        }
    }

}
