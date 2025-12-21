package net.kapitencraft.kap_lib.attribute.event.handler;

import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.kapitencraft.kap_lib.attribute.TimedModifiers;
import net.kapitencraft.kap_lib.core.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.kapitencraft.kap_lib.core.helpers.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class AttributeEvents {

    @SubscribeEvent
    public static void joinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            CompoundTag tag = player.getPersistentData();
            if (tag.contains("Health", Tag.TAG_FLOAT)) {
                player.setHealth(tag.getFloat("Health"));
            }
        }
    }

    @SubscribeEvent
    public static void modArrowEnchantments(ArrowLooseEvent event) {
        event.setCharge((int) (event.getCharge() * event.getEntity().getAttributeValue(ExtraAttributes.DRAW_SPEED) / 100));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void healthRegenRegister(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (living.getAttribute(ExtraAttributes.VITALITY) != null) {
            double vitality = living.getAttributeValue(ExtraAttributes.VITALITY);
            event.setAmount(event.getAmount() * (1 + (float) vitality / 100));
        }
    }

    private static boolean canJump(Player player) {
        return !player.onGround() && !(player.isPassenger() || player.getAbilities().flying) && !(player.isInWater() || player.isInLava());
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
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDrops(BlockDropsEvent event) {
        if (event.getBreaker() instanceof Player player) {
            double scale = AttributeHelper.getExperienceScale(player);
            event.setDroppedExperience((int) (event.getDroppedExperience() * scale));
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null) {
            event.setDroppedExperience((int) (event.getDroppedExperience() * AttributeHelper.getExperienceScale(player)));
        }
    }
}
