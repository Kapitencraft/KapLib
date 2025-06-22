package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.client.particle.animation.elements.MoveTowardsBBElement;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.EntityBBSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.EitherTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.EntityRemovedTerminator;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.enchantments.abstracts.ExtendedCalculationEnchantment;
import net.kapitencraft.kap_lib.enchantments.abstracts.IToolEnchantment;
import net.kapitencraft.kap_lib.enchantments.abstracts.ModBowEnchantment;
import net.kapitencraft.kap_lib.helpers.*;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.S2C.DisplayTotemActivationPacket;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.item.combat.totem.AbstractTotemItem;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.util.DamageCounter;
import net.kapitencraft.kap_lib.util.FerociousDamageSource;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@ApiStatus.Internal
@Mod.EventBusSubscriber
public class DamageEvents {
    private DamageEvents() {}//dummy constructor (do not call)

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void miscDamageEvents(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntity();
        LivingEntity attacker = MiscHelper.getAttacker(event.getSource());
        event.setAmount(BonusManager.attackEvent(attacked, attacker, MiscHelper.getDamageType(event.getSource()), event.getAmount()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void utilDamage(LivingDamageEvent event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        boolean dodge = false;
        double dodgePercentage = AttributeHelper.getSaveAttributeValue(ExtraAttributes.DODGE.get(), attacked);
        if (dodgePercentage > 0) {
            if (MathHelper.chance(dodgePercentage / 100, attacked) && ((!source.is(DamageTypeTags.BYPASSES_ARMOR) && !source.is(DamageTypeTags.IS_FALL) && !source.is(DamageTypeTags.IS_FIRE)) || source.is(DamageTypes.STALAGMITE))) {
                dodge = true;
                event.setAmount(0);
            }
        }
        MiscHelper.createDamageIndicator(attacked, event.getAmount(), dodge ? "dodge" : source.getMsgId());
        DamageCounter.increaseDamage(event.getAmount());
    }

    @SubscribeEvent
    public static void critDamageRegister(CriticalHitEvent event) {
        Player attacker = event.getEntity();
        if (event.isVanillaCritical() || AttributeHelper.getSaveAttributeValue(ExtraAttributes.CRIT_CHANCE.get(), attacker) / 100 > Math.random()) {
            event.setResult(Event.Result.ALLOW);
            event.setDamageModifier((float) (1 + AttributeHelper.getSaveAttributeValue(ExtraAttributes.CRIT_DAMAGE.get(), attacker) / 100));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void ferocityRegister(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        LivingEntity attacker = MiscHelper.getAttacker(source);
        if (attacker == null || MiscHelper.getDamageType(source) != MiscHelper.DamageType.MELEE) {
            return;
        }
        if (attacker.getAttribute(ExtraAttributes.FEROCITY.get()) != null) {
            double ferocity = source instanceof FerociousDamageSource damageSource ? damageSource.ferocity : attacker.getAttributeValue(ExtraAttributes.FEROCITY.get());
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
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void damageBonusRegister(LivingHurtEvent event) {
        LivingEntity attacked = event.getEntity();
        if (event.getSource().getDirectEntity() instanceof Arrow arrow) {
            CompoundTag tag = arrow.getPersistentData();
            event.setAmount(ModBowEnchantment.loadFromTag(attacked, tag, ModBowEnchantment.ExePhase.HIT, event.getAmount(), arrow));
            if (tag.getInt("OverloadEnchant") > 0 && arrow.isCritArrow()) {
                if (MathHelper.chance(0.1, arrow.getOwner())) event.setAmount((float) (event.getAmount() * 1 + (tag.getInt("OverloadEnchant") * 0.1)));
            }
            return;
        }

        DamageSource source = event.getSource();
        @Nullable LivingEntity attacker = MiscHelper.getAttacker(source);
        if (attacker == null) { return; }
        MiscHelper.DamageType type = MiscHelper.getDamageType(source);
        ItemStack stack = attacker.getMainHandItem();
        Map<Enchantment, Integer> enchantments = stack.getAllEnchantments();
        if (enchantments != null) {
            event.setAmount(ExtendedCalculationEnchantment.runWithPriority(stack, attacker, attacked, event.getAmount(), type, source));
            MiscHelper.getArmorEquipment(attacked).forEach(stack1 -> event.setAmount(ExtendedCalculationEnchantment.runWithPriority(stack1, attacker, attacked, event.getAmount(), type, source)));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damageAttributeRegister(LivingHurtEvent event) {
        @Nullable LivingEntity attacker = MiscHelper.getAttacker(event.getSource());
        if (attacker == null) return;
        if (MiscHelper.getDamageType(event.getSource()) == MiscHelper.DamageType.MELEE && attacker.getAttributes().hasAttribute(ExtraAttributes.STRENGTH.get())) {
            double Strength = AttributeHelper.getSaveAttributeValue(ExtraAttributes.STRENGTH.get(), attacker);
            MathHelper.mul(event::getAmount, event::setAmount, (float) (1 + Strength / 100));
        }
        double doubleJump = AttributeHelper.getSaveAttributeValue(ExtraAttributes.DOUBLE_JUMP.get(), attacker);
        LivingEntity attacked = event.getEntity();
        if (AttributeHelper.getSaveAttributeValue(ExtraAttributes.ARMOR_SHREDDER.get(), attacker) != -1) {
            double armorShredder = AttributeHelper.getSaveAttributeValue(ExtraAttributes.ARMOR_SHREDDER.get(), attacker);
            MiscHelper.getArmorEquipment(attacked)
                    .forEach(stack -> stack.hurt((int) (armorShredder / 3), attacked.level().getRandom(), attacker instanceof ServerPlayer serverPlayer ? serverPlayer : null));
        }
        double liveSteal = AttributeHelper.getSaveAttributeValue(ExtraAttributes.LIVE_STEAL.get(), attacker);
        if (!event.getSource().isIndirect() && liveSteal > 0) {
            if (attacker.level() instanceof ServerLevel sL) {
                ParticleAnimation.builder()
                        .spawn(EntityBBSpawner.builder()
                                .setParticle(new DustParticleOptions(Vec3.fromRGB24(0x800000).toVector3f(), .3f))
                                .target(attacked)
                                .perTick(150)
                                .scaleX(1.3f).scaleY(1.1f)
                        ).then(MoveTowardsBBElement.builder()
                                .target(attacker)
                                .duration(30)
                        ).finalizes(RemoveParticleFinalizer.builder())
                        .spawnTime(ParticleAnimation.SpawnTime.once())
                        .terminatedWhen(EitherTerminator.with(
                                TimedTerminator.ticks(20),
                                EntityRemovedTerminator.builder(attacked),
                                EntityRemovedTerminator.builder(attacker)
                        ))
                        .sendToAllPlayers(sL);
            }
            attacker.heal(Math.min((float) liveSteal, event.getAmount()));
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        event.setCanceled(!RequirementManager.meetsItemRequirementsFromEvent(event, EquipmentSlot.MAINHAND));
    }

    @SubscribeEvent
    public static void shieldBlockEnchantments(ShieldBlockEvent event) {
        LivingEntity attacked = event.getEntity();
        @Nullable LivingEntity attacker = MiscHelper.getAttacker(event.getDamageSource());
        if (attacker == null) { return; }
        ItemStack stack = attacker.getUseItem();
        MiscHelper.DamageType type = MiscHelper.getDamageType(event.getDamageSource());
        Map<Enchantment, Integer> enchantments = stack.getAllEnchantments();
        if (enchantments != null && !enchantments.isEmpty()) {
            MapStream.of(enchantments)
                    .mapKeys(ExtendedCalculationEnchantment.class::cast)
                    .filterKeys(Objects::nonNull)
                    .filterKeys(ench -> ench instanceof IToolEnchantment)
                    .forEach((enchantment, integer) -> enchantment.tryExecute(integer, stack, attacker, attacked, event.getBlockedDamage(), type, event.getDamageSource()));
        }
    }

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
                    ModMessages.sendToClientPlayer(new DisplayTotemActivationPacket(stack.copy(), player.getId()), player);
                    stack.shrink(1);
                    break;
                }
            }
        }
        if (!event.isCanceled()) {
            BonusManager.deathEvent(toDie, event.getSource());
        }
    }
}
