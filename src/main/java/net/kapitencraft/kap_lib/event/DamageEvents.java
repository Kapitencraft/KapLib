package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.client.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.client.particle.animation.elements.MoveTowardsBBElement;
import net.kapitencraft.kap_lib.client.particle.animation.finalizers.RemoveParticleFinalizer;
import net.kapitencraft.kap_lib.client.particle.animation.spawners.EntityBBSpawner;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.EntityRemovedTerminatorTrigger;
import net.kapitencraft.kap_lib.client.particle.animation.terminators.TimedTerminator;
import net.kapitencraft.kap_lib.enchantments.abstracts.EnchantmentBowEffect;
import net.kapitencraft.kap_lib.enchantments.abstracts.EnchantmentCountEffect;
import net.kapitencraft.kap_lib.helpers.*;
import net.kapitencraft.kap_lib.io.network.S2C.DisplayTotemActivationPacket;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.item.combat.totem.AbstractTotemItem;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.ExtraEnchantmentEffectComponents;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
@EventBusSubscriber
public class DamageEvents {
    private DamageEvents() {}//dummy constructor (do not call)

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void miscDamageEvents(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        LivingEntity attacker = MiscHelper.getAttacker(event.getSource());
        event.setNewDamage(BonusManager.attackEvent(attacked, attacker, MiscHelper.getDamageType(event.getSource()), event.getNewDamage()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void utilDamage(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        boolean dodge = false;
        double dodgePercentage = AttributeHelper.getSaveAttributeValue(ExtraAttributes.DODGE, attacked);
        if (dodgePercentage > 0) {
            if (MathHelper.chance(dodgePercentage / 100, attacked) && ((!source.is(DamageTypeTags.BYPASSES_ARMOR) && !source.is(DamageTypeTags.IS_FALL) && !source.is(DamageTypeTags.IS_FIRE)) || source.is(DamageTypes.STALAGMITE))) {
                dodge = true;
                event.setNewDamage(0);
            }
        }
        MiscHelper.createDamageIndicator(attacked, event.getNewDamage(), dodge ? "dodge" : source.getMsgId());
        DamageCounter.increaseDamage(event.getNewDamage());
    }

    @SubscribeEvent
    public static void critDamageRegister(CriticalHitEvent event) {
        Player attacker = event.getEntity();
        if (event.isVanillaCritical() || AttributeHelper.getSaveAttributeValue(ExtraAttributes.CRIT_CHANCE, attacker) / 100 > Math.random()) {
            event.setCriticalHit(true);
            event.setDamageMultiplier((float) (1 + AttributeHelper.getSaveAttributeValue(ExtraAttributes.CRIT_DAMAGE, attacker) / 100));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void ferocityRegister(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        LivingEntity attacker = MiscHelper.getAttacker(source);
        if (attacker == null || MiscHelper.getDamageType(source) != MiscHelper.DamageType.MELEE) {
            return;
        }
        if (attacker.getAttribute(ExtraAttributes.FEROCITY) != null) {
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
            });
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damageAttributeRegister(LivingDamageEvent.Pre event) {
        @Nullable LivingEntity attacker = MiscHelper.getAttacker(event.getSource());
        if (attacker == null) return;
        if (MiscHelper.getDamageType(event.getSource()) == MiscHelper.DamageType.MELEE && attacker.getAttributes().hasAttribute(ExtraAttributes.STRENGTH)) {
            double Strength = AttributeHelper.getSaveAttributeValue(ExtraAttributes.STRENGTH, attacker);
            MathHelper.mul(event::getNewDamage, event::setNewDamage, (float) (1 + Strength / 100));
        }
        double armorShredder = AttributeHelper.getSaveAttributeValue(ExtraAttributes.ARMOR_SHREDDER, attacker);
        LivingEntity attacked = event.getEntity();
        if (armorShredder > 0 && attacked.level() instanceof ServerLevel sL) {
            MiscHelper.getArmorEquipment(attacked)
                    .forEach(stack -> stack.hurtAndBreak((int) (armorShredder / 3), sL, attacker instanceof ServerPlayer serverPlayer ? serverPlayer : null, i -> {}));
        }
        double liveSteal = AttributeHelper.getSaveAttributeValue(ExtraAttributes.LIVE_STEAL, attacker);
        if (event.getSource().isDirect() && liveSteal > 0) {
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
                        .terminatedWhen(TimedTerminator.ticks(20))
                        .terminatedWhen(EntityRemovedTerminatorTrigger.create(attacked))
                        .terminatedWhen(EntityRemovedTerminatorTrigger.create(attacker))
                        .sendToAllPlayers();
            }
            attacker.heal(Math.min((float) liveSteal, event.getNewDamage()));
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        event.setCanceled(!RequirementManager.meetsItemRequirementsFromEvent(event, EquipmentSlot.MAINHAND));
    }

    @SubscribeEvent
    public static void shieldBlockEnchantments(LivingShieldBlockEvent event) {
        LivingEntity attacked = event.getEntity();
        @Nullable LivingEntity attacker = MiscHelper.getAttacker(event.getDamageSource());
        if (attacker == null) { return; }
        ItemStack stack = attacker.getUseItem();
        MiscHelper.DamageType type = MiscHelper.getDamageType(event.getDamageSource());
        EnchantedItemInUse shield = new EnchantedItemInUse(stack, attacked.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, attacked);
        if (attacker.level() instanceof ServerLevel serverLevel) {
            EnchantmentHelper.runIterationOnItem(stack, (enchantment, level) -> {
                List<TargetedConditionalEffect<EnchantmentEntityEffect>> effects = enchantment.value().getEffects(ExtraEnchantmentEffectComponents.SHIELD_BLOCK.get());
                LootContext lootContext = Enchantment.damageContext(serverLevel, level, event.getEntity(), event.getDamageSource());
                effects.forEach(effect -> {
                    if (effect.matches(lootContext)) {
                        Entity entity = switch (effect.affected()) {
                            case ATTACKER -> attacker;
                            case DAMAGING_ENTITY -> event.getDamageSource().getDirectEntity();
                            case VICTIM -> attacked;
                        };
                        effect.effect().apply(serverLevel, level, shield, entity, attacked.position());
                    }
                });
            });
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
                    PacketDistributor.sendToPlayer(player, new DisplayTotemActivationPacket(stack.copy(), player.getId()));
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
