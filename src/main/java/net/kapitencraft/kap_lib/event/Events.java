package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.ExtraComponents;
import net.kapitencraft.kap_lib.client.glyph.player_head.PlayerHeadAllocator;
import net.kapitencraft.kap_lib.collection.Queue;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.cooldown.CooldownsProvider;
import net.kapitencraft.kap_lib.enchantments.abstracts.ModBowEnchantment;
import net.kapitencraft.kap_lib.helpers.*;
import net.kapitencraft.kap_lib.inventory.wearable.WearableProvider;
import net.kapitencraft.kap_lib.inventory.wearable.Wearables;
import net.kapitencraft.kap_lib.io.network.ModMessages;
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
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * event listeners for KapLib.
 * <br>there shouldn't be any reason for Modders to use this class
 */
@ApiStatus.Internal
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
        if (!dontCancel.contains(event.getClass()) && !RequirementManager.meetsItemRequirementsFromEvent(event, EquipmentSlot.MAINHAND) && event.isCancelable()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void addRequirementListener(AddReloadListenerEvent event) {
        event.addListener(RequirementManager.instance = new RequirementManager());
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
            ModMessages.sendToClientPlayer(new SyncRequirementsPacket(RequirementManager.instance), serverPlayer);
            ModMessages.sendToClientPlayer(new SyncBonusesPacket(BonusManager.instance), serverPlayer);
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
        event.setCharge((int) (event.getCharge() * event.getEntity().getAttributeValue(ExtraAttributes.DRAW_SPEED.get()) / 100));
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
            AttributeInstance manaInst = player.getAttribute(ExtraAttributes.MANA.get());
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
            player.getPersistentData().putDouble("Mana", player.getAttributeValue(ExtraAttributes.MANA.get()));
        }
        if (event.getEntity().level().isClientSide()) {
            TerminatorTriggers.ENTITY_REMOVED.get().trigger(event.getEntity().getId());
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.LevelTickEvent event) {
        if (event.level instanceof ServerLevel serverLevel) {
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
        if (living.getAttribute(ExtraAttributes.VITALITY.get()) != null) {
            double vitality = living.getAttributeValue(ExtraAttributes.VITALITY.get());
            event.setAmount(event.getAmount() * (1 + (float) vitality / 100));
        }
    }

    @SubscribeEvent
    public static void onGameShuttingDown(GameShuttingDownEvent event) {
        PlayerHeadAllocator.getInstance().shutDown();
    }


    public static final String DOUBLE_JUMP_ID = "currentDoubleJump";

    @SubscribeEvent
    public static void entityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        Cooldowns.get(living).tick();
        BonusHelper.tickEnchantments(living);
        CompoundTag tag = living.getPersistentData();
        if (living instanceof Player player) {
            if (!player.onGround()) {
                if (canJump(player) && tag.getInt(DOUBLE_JUMP_ID) < player.getAttributeValue(ExtraAttributes.DOUBLE_JUMP.get())) {
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
        if (living instanceof Mob mob) {
            if (mob.getTarget() != null && mob.getTarget().isInvisible()) {
                mob.setTarget(null);
            }
        }
    }

    private static boolean canJump(Player player) {
        return !player.onGround() && !(player.isPassenger() || player.getAbilities().flying) && !(player.isInWater() || player.isInLava());
    }

    @SubscribeEvent
    public static void addWearableToPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity living) {
            event.addCapability(KapLibMod.res("wearable"), new WearableProvider(living));
            event.addCapability(KapLibMod.res("cooldowns"), new CooldownsProvider(living));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        event.setExpToDrop((int) (event.getExpToDrop() * AttributeHelper.getExperienceScale(player)));
    }

    @SubscribeEvent
    public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null) {
            event.setDroppedExperience((int) (event.getDroppedExperience() * AttributeHelper.getExperienceScale(player)));
        }
    }

}
