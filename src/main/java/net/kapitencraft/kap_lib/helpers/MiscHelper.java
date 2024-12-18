package net.kapitencraft.kap_lib.helpers;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.font.effect.EffectsStyle;
import net.kapitencraft.kap_lib.client.font.effect.GlyphEffect;
import net.kapitencraft.kap_lib.client.particle.DamageIndicatorParticleOptions;
import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.kapitencraft.kap_lib.util.Color;
import net.kapitencraft.kap_lib.util.ExtraRarities;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MiscHelper {
    //EAST = new Rotation("x+", 90, 1);
    //WEST = new Rotation("x-",270, 3);
    //SOUTH = new Rotation("z+", 180, 2);
    //NORTH = new Rotation("z-", 360, 4);


    /**
     * @param stack the {@link ItemStack} to get the slot from
     * @return the {@link EquipmentSlot} dedicated to this stack
     */
    @Contract("null -> fail")
    public static EquipmentSlot getSlotForStack(ItemStack stack) {
        return LivingEntity.getEquipmentSlotForItem(stack);
    }


    @Contract("null, _ -> param2; !null, _ -> param1")
    public static <T> T nonNullOr(@Nullable T value, @NotNull T or) {
        return value == null ? or : value;
    }

    /**
     * needed in order for the Mixin invoker on serverside not to cry
     */
    public static void sendManaBoostParticles(Entity target, RandomSource random, Vec3 delta) {
        ClientHelper.sendElytraBoostParticles(target, random, delta, new Color(0, 0, 1, 1), new Color(.5f, 0, .5f, 1));
    }

    @Contract("null -> fail")
    public static void swapHands(LivingEntity living) {
        ItemStack mainHand = living.getMainHandItem();
        living.setItemInHand(InteractionHand.MAIN_HAND, living.getOffhandItem());
        living.setItemInHand(InteractionHand.OFF_HAND, mainHand);
    }

    /**
     * @param style the Style to add the effect to
     * @param effect the effect to be added
     * @return the new Style with applied effect
     */
    public static Style withSpecial(Style style, GlyphEffect effect) {
        Style newStyle = style.withClickEvent(style.getClickEvent());
        EffectsStyle effectsStyle = EffectsStyle.of(newStyle);
        effectsStyle.addEffect(effect);
        return newStyle;
    }

    /**
     * method to repair items similar to {@link net.minecraft.world.item.enchantment.MendingEnchantment}
     * @param player player to repair items on
     * @param value the base amount of repair capacity
     * @param ench the enchantment this calculation is based on (see {@link net.minecraft.world.item.enchantment.MendingEnchantment})
     * @return the amount of capacity that hasn't been used
     */
    @Contract("null, _, _ -> fail; _, _, null -> fail")
    public static int repairPlayerItems(Player player, int value, Enchantment ench) {
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(ench, player, ItemStack::isDamaged);
        if (entry != null) {
            ItemStack itemstack = entry.getValue();
            int i = Math.min((int) (value * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
            itemstack.setDamageValue(itemstack.getDamageValue() - i);
            int j = value - i / 2;
            return j > 0 ? repairPlayerItems(player, j, ench) : 0;
        } else {
            return value;
        }

    }

    public static boolean is(Item item, TagKey<Item> tagKey) {
        return item.builtInRegistryHolder().is(tagKey);
    }

    /**
     * method to get the enchantment level of a stack and execute the consumer when above 0
     * @param stack the stack to check the enchantment level of
     * @param enchantment the enchantment to check
     * @param enchConsumer the method to be executed when level > 0
     */
    public static void getEnchantmentLevelAndDo(ItemStack stack, Enchantment enchantment, Consumer<Integer> enchConsumer) {
        if (stack.getEnchantmentLevel(enchantment) > 0) {
            enchConsumer.accept(stack.getEnchantmentLevel(enchantment));
        }
    }

    /**
     * method to get the Rarity of an {@link ItemStack}
     * @param rarity the stack's item's base rarity
     * @param stack the stack to check the rarity on
     * @return the rarity after calculation enchantment mods
     */
    @Contract("null, _ -> fail; _, null -> fail")
    public static Rarity getFinalRarity(Rarity rarity, ItemStack stack) {
        if (!stack.isEnchanted()) {
            return rarity;
        } else {
            return switch (rarity) {
                case COMMON -> Rarity.UNCOMMON;
                case UNCOMMON -> Rarity.RARE;
                case RARE -> Rarity.EPIC;
                case EPIC -> ExtraRarities.LEGENDARY;
                default -> rarity == ExtraRarities.LEGENDARY ? ExtraRarities.MYTHIC : rarity == ExtraRarities.MYTHIC ? ExtraRarities.DIVINE : Rarity.COMMON;
            };
        }
    }


    /**
     * a simple method to get a difficulty sensitive value
     * @param difficulty the difficulty to scan for
     * @param easy the value if difficulty is easy
     * @param medium the value if difficulty is medium
     * @param hard the value if difficulty is hard
     * @param peaceful the value if difficulty is peaceful
     * @return the value from the check
     */
    public static <T> T forDifficulty(Difficulty difficulty, T easy, T medium, T hard, T peaceful) {
        return switch (difficulty) {
            case EASY -> easy;
            case NORMAL -> medium;
            case HARD -> hard;
            case PEACEFUL -> peaceful;
        };
    }

    /**
     * method to do code when a t is not null
     * @param t value to check null of
     */
    public static <T> void ifNonNull(@Nullable T t, Consumer<T> toDo) {
        if (t != null) {
            toDo.accept(t);
        }
    }

    /**
     * simular method to ifNonNull but with return value and supplier for null
     * @param t method to check null
     * @param function transfer-method to convert it into the return value
     * @param defaulted {@link Supplier} to get a value if t was null
     * @return mapped result (either from function or defaulted)
     */
    public static <T, K> @NotNull K ifNonNullOrDefault(@Nullable T t, Function<T, K> function, Supplier<K> defaulted) {
        if (t != null) {
            return function.apply(t);
        }
        return defaulted.get();
    }

    /**
     * @param rarity to add to the {@link Item.Properties}
     * @return a {@link Item.Properties} with the rarity
     */
    @Contract("_ -> new")
    public static Item.Properties rarity(Rarity rarity) {
        return new Item.Properties().rarity(rarity);
    }


    /**
     * method to add an achievement to a player
     * @param player player to add achievement to
     * @param achievementName name of the achievement
     * @return true if the achievement has been awarded, false otherwise
     * @deprecated use custom achievement triggers
     */
    @Deprecated(forRemoval = true)
    public static boolean awardAchievement(Player player, String achievementName) {
        if (player instanceof ServerPlayer serverPlayer) {
            ServerAdvancementManager manager = serverPlayer.server.getAdvancements();
            Advancement adv = manager.getAdvancement(new ResourceLocation(achievementName));
            PlayerAdvancements advancements = serverPlayer.getAdvancements();
            if (adv != null) {
                AdvancementProgress progress = advancements.getOrStartProgress(adv);
                if (!progress.isDone()) {
                    for (String s : progress.getRemainingCriteria()) advancements.award(adv, s);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param provider mapper to get value from
     * @param defaultValue returned if none of the values does
     * @param key key to search for
     * @param values values to search in
     * @return the found value or defaultValue if noting was returned
     */
    public static <T, K> T getValue(Function<T, K> provider, T defaultValue, K key, T... values) {
        for (T t : values) {
            if (provider.apply(t).equals(key)) {
                return t;
            }
        }
        return defaultValue;
    }

    /**
     * a method to delay {@code run} by delayTicks
     * @author Kapitencraft
     * @param delayTicks time (in ticks) to delay
     * @param run runnable to execute at the end of the delay
     */
    public static void schedule(int delayTicks, Runnable run) {
        new Object() {
            private int ticks = 0;
            private float waitTicks;

            public void start(int waitTicks) {
                this.waitTicks = waitTicks;
                MinecraftForge.EVENT_BUS.register(this);
            }
            @SubscribeEvent
            public void tick(TickEvent.ServerTickEvent event) {
                if (event.phase == TickEvent.Phase.END) {
                    this.ticks += 1;
                    if (this.ticks >= this.waitTicks)
                        end();
                }
            }
            private void end() {
                MinecraftForge.EVENT_BUS.unregister(this);
                run.run();
            }
        }.start(delayTicks);
    }


    /**
     * method to teleport entity maxRange blocks forward, checking block hits
     * @param entity entity to teleport
     * @param maxRange maximal range of the teleport, reduced when hitting a block
     * @return if the entity has been teleported
     */
    public static boolean saveTeleport(Entity entity, double maxRange) {
        try {
            Vec3 targetPos = entity.getLookAngle().scale(maxRange);
            entity.stopRiding();
            entity.move(MoverType.SELF, targetPos);
        } catch (Exception e) {
            KapLibMod.LOGGER.warn("error trying to teleport entity '{}': {}", entity, e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * method to directly teleport a player to the target location
     * sending sounds and resetting fall-distance
     * @param entity entity to teleport
     * @param teleportPosition target teleportation location
     */
    public static void teleport(Entity entity, Vec3 teleportPosition) {
        entity.teleportTo(teleportPosition.x, teleportPosition.y, teleportPosition.z);
        entity.fallDistance = 0;
        entity.level().playSound(entity, BlockPos.containing(entity.position()), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
    }

    /**
     * @param times amount of times the consumer will be called
     * @param consumer usage of the integer with the index of the iteration
     */
    public static void repeat(int times, Consumer<Integer> consumer) {
        for (int i = 0; i < times; i++) {
            consumer.accept(i);
        }
    }

    /**
     * @param components all components to merge to getter
     * @return the merged component
     */
    public static MutableComponent buildComponent(MutableComponent... components) {
        MutableComponent empty = Component.empty();
        for (MutableComponent component : components) {
            empty.append(component);
        }
        return empty;
    }


    /**
     * method to simply get the attacker from a {@link DamageSource}
     * @param source {@link DamageSource} to get attacker from
     * @return the {@link Nullable} {@link LivingEntity} to get from the damagesource
     */
    public static @Nullable LivingEntity getAttacker(@NotNull DamageSource source) {
        return source.getEntity() instanceof LivingEntity living ? living : null;
    }

    /**
     * {@link DamageType} to get from a DamageSource
     * used for Enchantments
     * @param source source to get DamageType from
     * @return DamageType from the source
     */
    @Contract("null -> fail")
    public static DamageType getDamageType(DamageSource source) {
        if (source.is(ExtraTags.DamageTypes.MAGIC)) {
            return DamageType.MAGIC;
        }
        if (source.getEntity() != null) {
            if (source.getDirectEntity() == source.getEntity()) {
                return DamageType.MELEE;
            }
            return DamageType.RANGED;
        }
        return DamageType.MISC;
    }

    /**
     * the damage types for enchantment calculation
     */
    public enum DamageType {
        RANGED,
        MELEE,
        MAGIC,
        MISC
    }

    public static ArmorStand createMarker(Vec3 pos, Level level, boolean invisible) {
        ArmorStand stand = new ArmorStand(level, pos.x, pos.y, pos.z);
        CompoundTag tag = stand.getPersistentData();
        tag.putBoolean("Marker", true);
        stand.setInvulnerable(true);
        stand.setInvisible(invisible);
        stand.setNoGravity(true);
        stand.setBoundingBox(new AABB(0,0,0,0,0,0));
        return stand;
    }


    public static <T> T of(Supplier<T> sup) {
        return sup.get();
    }

    public static void createDamageIndicator(LivingEntity entity, float amount, String type) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            float rangeOffset = entity.getBbHeight() / 2;
            ParticleHelper.sendParticles(serverLevel, new DamageIndicatorParticleOptions(TextHelper.damageIndicatorCoder(type), amount, rangeOffset), false, entity.getX(), entity.getY(), entity.getZ(), 1, 0, 0, 0, 0);
        }
    }

    @Nullable
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
        return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
    }

    private static Vec3 getUpdateForPos(Vec3 cam, LivingEntity living) {
        Vec3 livingPos = living.position();
        Vec3 delta = cam.subtract(livingPos);
        return MathHelper.clampLength(delta, 0.5);
    }

    public static final char HEART = '\u2661';

    public static ArmorStand createHealthIndicator(LivingEntity target) {
        ArmorStand marker = createMarker(target.position().add(0, 0.5, 0), target.level(), true);
        CompoundTag tag = marker.getPersistentData();
        tag.putBoolean("healthMarker", true);
        marker.setCustomName(Component.literal(HEART + " " + target.getHealth() + "/" + target.getMaxHealth()));
        marker.setCustomNameVisible(true);
        target.level().addFreshEntity(marker);
        return marker;
    }

    public static Rarity getItemRarity(Item item) {
        return item.getRarity(new ItemStack(item));
    }

    public static final EquipmentSlot[] ARMOR_EQUIPMENT = Arrays.stream(EquipmentSlot.values()).filter(EquipmentSlot::isArmor).toArray(EquipmentSlot[]::new);
    public static final EquipmentSlot[] WEAPON_SLOT = new EquipmentSlot[]{EquipmentSlot.MAINHAND};

    public static Stream<ItemStack> getArmorEquipment(LivingEntity living) {
        return Arrays.stream(ARMOR_EQUIPMENT).map(living::getItemBySlot);
    }
    public static int createCustomIndex(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 0;
            case CHEST -> 1;
            case LEGS -> 2;
            case FEET -> 3;
            case OFFHAND -> 4;
            case MAINHAND -> 5;
        };
    }

    public static boolean increaseEffectDuration(LivingEntity living, MobEffect effect, int ticks) {
        if (living.hasEffect(effect)) {
            MobEffectInstance oldInstance = living.getEffect(effect);
            assert oldInstance != null;
            MobEffectInstance effectInstance = new MobEffectInstance(effect, oldInstance.getDuration() + ticks, oldInstance.getAmplifier(), oldInstance.isAmbient(), oldInstance.isVisible(), oldInstance.showIcon(), oldInstance, oldInstance.getFactorData());
            living.removeEffect(effect);
            living.addEffect(effectInstance);
            return false;
        } else {
            living.addEffect(new MobEffectInstance(effect, 1, ticks));
            return true;
        }
    }



    public static char[] append(char[] in, char toAppend) {
        char[] copy = new char[in.length + 1];
        repeat(in.length, integer -> copy[integer] = in[integer]);
        copy[in.length] = toAppend;
        return copy;
    }

    @Contract("null, _, _ -> fail; _, _, _ -> param1")
    public static List<ItemStack> shrinkDrops(List<ItemStack> drops, Item item, final int amount) {
        repeat(drops.size(), i -> {
            int varAmount = amount;
            ItemStack stack = drops.get(i);
            if (stack.getItem() == item) {
                while (varAmount > 0) {
                    stack.shrink(1);
                    varAmount--;
                    if (stack.isEmpty()) {
                        drops.remove(i);
                        break;
                    }
                }
            }
        });
        return drops;
    }
}