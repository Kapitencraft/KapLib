package net.kapitencraft.kap_lib.attribute.event.handler;

import net.kapitencraft.kap_lib.attribute.AttributeAttachmentTypes;
import net.kapitencraft.kap_lib.attribute.AttributeModule;
import net.kapitencraft.kap_lib.attribute.ExtraAttributes;
import net.kapitencraft.kap_lib.attribute.timed.TimedModifiers;
import net.kapitencraft.kap_lib.attribute.compat.ParticleCompat;
import net.kapitencraft.kap_lib.core.util.Modules;
import net.kapitencraft.kap_lib.core.helpers.*;
import net.kapitencraft.kap_lib.attribute.damage.FerociousDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

/**
 * internal
 */
@ApiStatus.Internal
@EventBusSubscriber(modid = AttributeModule.MODULE_ID)
public class AttributeEvents {

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ExtraAttributes.DRAW_SPEED);
        event.add(EntityType.PLAYER, ExtraAttributes.FISHING_SPEED);
        event.add(EntityType.PLAYER, ExtraAttributes.WISDOM);
    }

    @SubscribeEvent
    private static void joinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            CompoundTag tag = player.getPersistentData();
            if (tag.contains("Health", Tag.TAG_FLOAT)) {
                player.setHealth(tag.getFloat("Health"));
            }
        }
    }

    @SubscribeEvent
    private static void modArrowEnchantments(ArrowLooseEvent event) {
        event.setCharge((int) (event.getCharge() * event.getEntity().getAttributeValue(ExtraAttributes.DRAW_SPEED) / 100));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void healthRegenRegister(LivingHealEvent event) {
        LivingEntity living = event.getEntity();
        if (living.getAttribute(ExtraAttributes.VITALITY) != null) {
            double vitality = living.getAttributeValue(ExtraAttributes.VITALITY);
            event.setAmount(event.getAmount() * (1 + (float) vitality / 100));
        }
    }

    private static boolean canJump(Entity entity) {
        return !entity.onGround() &&
                !(entity.isPassenger() || entity instanceof Player player && player.mayFly()) && !entity.isInFluidType();
    }

    @SubscribeEvent
    private static void onPlayerTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            if (living.getAttributes().hasAttribute(ExtraAttributes.DOUBLE_JUMP) && !entity.onGround()) {
                if (canJump(entity) && living.getData(AttributeAttachmentTypes.DOUBLE_JUMPS) < living.getAttributeValue(ExtraAttributes.DOUBLE_JUMP)) {
                    if (living.jumping && living.noJumpDelay <= 0) {
                        ParticleHelper.sendAlwaysVisibleParticles(ParticleTypes.CLOUD, entity.level(), entity.getX(), entity.getY(), entity.getZ(), 0.25, 0.0, 0.25, 0, 0, 0, 15);
                        living.noJumpDelay = 10;
                        entity.fallDistance = 0;
                        living.jumpFromGround();
                        living.setData(AttributeAttachmentTypes.DOUBLE_JUMPS, living.getData(AttributeAttachmentTypes.DOUBLE_JUMPS) + 1);
                    }
                }
            } else if (living.hasData(AttributeAttachmentTypes.DOUBLE_JUMPS)) {
                living.setData(AttributeAttachmentTypes.DOUBLE_JUMPS, 0);
            }
            TimedModifiers.get(living).tick(living);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    private static void onBlockDrops(BlockDropsEvent event) {
        if (event.getBreaker() instanceof Player player) {
            double scale = ExtraAttributes.getExperienceScale(player);
            event.setDroppedExperience((int) (event.getDroppedExperience() * scale));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    private static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null) {
            event.setDroppedExperience((int) (event.getDroppedExperience() * ExtraAttributes.getExperienceScale(player)));
        }
    }

    @SubscribeEvent
    private static void critDamageRegister(CriticalHitEvent event) {
        Player attacker = event.getEntity();
        if (event.isVanillaCritical() || ExtraAttributes.isArtificialCrit(attacker)) {
            event.setCriticalHit(true);
            event.setDamageMultiplier((float) (1 + AttributeHelper.getSaveAttributeValue(ExtraAttributes.CRIT_DAMAGE, attacker) / 100));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    private static void ferocityRegister(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        LivingEntity attacker = MiscHelper.getAttacker(source);
        if (attacker == null || !source.isDirect() || source.is(DamageTypes.THORNS)) {
            return;
        }
        double ferocity = source instanceof FerociousDamageSource damageSource ? damageSource.ferocity : attacker.getAttributeValue(ExtraAttributes.FEROCITY);
        if (MathHelper.chance(ferocity / 100, attacker)) {
            MiscHelper.schedule(40, () -> {
                float ferocityDamage = (float) (source instanceof FerociousDamageSource ferociousDamageSource ? ferociousDamageSource.damage :
                        source.getEntity() instanceof AbstractArrow arrow ? arrow.getBaseDamage() : attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
                if (attacked.isDeadOrDying()) return;
                attacked.level().playSound(attacked, attacked.getOnPos(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.HOSTILE, 1f, 0.5f);
                attacked.hurt(FerociousDamageSource.create(attacker, (ferocity - 100), ferocityDamage), ferocityDamage);
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private static void damageAttributeRegister(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        @Nullable LivingEntity attacker = MiscHelper.getAttacker(source);
        if (attacker == null) return;
        if (source.isDirect() && !source.is(DamageTypes.THORNS) && attacker.getAttributes().hasAttribute(ExtraAttributes.STRENGTH)) {
            double strength = AttributeHelper.getSaveAttributeValue(ExtraAttributes.STRENGTH, attacker);
            event.setNewDamage(event.getNewDamage() * (float) (1 + strength / 100));
        }
        double armorShredder = AttributeHelper.getSaveAttributeValue(ExtraAttributes.ARMOR_SHREDDER, attacker);
        LivingEntity attacked = event.getEntity();
        if (armorShredder > 0 && attacked.level() instanceof ServerLevel sL) {
            MiscHelper.getArmorEquipment(attacked)
                    .forEach(stack -> stack.hurtAndBreak((int) (armorShredder / 3), sL, attacker instanceof ServerPlayer serverPlayer ? serverPlayer : null, i -> {}));
        }
        if (source.getDirectEntity() instanceof AbstractArrow arrow) {
            if (arrow.getOwner() instanceof LivingEntity owner) {
                if (arrow.isCritArrow() || ExtraAttributes.isArtificialCrit(owner)) {
                    event.setNewDamage(event.getNewDamage() * (1f + (float) owner.getAttributeValue(ExtraAttributes.CRIT_DAMAGE) / 100f));
                }
            }
        }
        double liveSteal = AttributeHelper.getSaveAttributeValue(ExtraAttributes.LIFE_STEAL, attacker);
        if (source.isDirect() && !source.is(DamageTypes.THORNS) && liveSteal > 0) {
            if (attacker.level() instanceof ServerLevel && Modules.isParticleActive()) {
                ParticleCompat.sendLifeStealAnimation(attacked, attacker);
            }
            attacker.heal(Math.min((float) liveSteal, event.getNewDamage()));
        }
    }
}
