package net.kapitencraft.kap_lib.helpers;

import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class EnchantmentHelperExtras {
    public static Enchantment.Builder ultimate(HolderGetter<Enchantment> enchantments, Enchantment.EnchantmentDefinition definition) {
        return Enchantment.enchantment(definition).exclusiveWith(enchantments.getOrThrow(ExtraTags.Enchantments.ULTIMATE));
    }

    public static Enchantment.EnchantmentDefinition bow(HolderGetter<Item> items, int weight, int maxLevel, Enchantment.Cost minCost, Enchantment.Cost maxCost, int anvilCost) {
        return Enchantment.definition(
                items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                weight,
                maxLevel,
                minCost,
                maxCost,
                anvilCost,
                EquipmentSlotGroup.MAINHAND
        );
    }


    /**
     * method to repair items similar to the mending enchantment
     * @param player player to repair items on
     * @param value the base amount of repair capacity
     * @param ench the enchantment component this calculation is based on
     * @return the amount of capacity that hasn't been used
     */
    public static int repairPlayerItems(@NotNull Player player, int value, @NotNull DataComponentType<?> ench) {
        Optional<EnchantedItemInUse> entry = EnchantmentHelper.getRandomItemWith(ench, player, ItemStack::isDamaged);
        if (entry.isPresent()) {
            ItemStack itemstack = entry.get().itemStack();
            int i = Math.min((int) (value * itemstack.getXpRepairRatio()), itemstack.getDamageValue());
            itemstack.setDamageValue(itemstack.getDamageValue() - i);
            int j = value - i / 2;
            return j > 0 ? repairPlayerItems(player, j, ench) : 0;
        }
        return value;
    }

    /**
     * method to get the enchantment level of a stack and execute the consumer when above 0
     * @param stack the stack to check the enchantment level of
     * @param enchantment the enchantment to check
     * @param enchConsumer the method to be executed when level > 0
     */
    public static void getEnchantmentLevelAndDo(ItemStack stack, Holder<Enchantment> enchantment, Consumer<Integer> enchConsumer) {
        if (stack.getEnchantmentLevel(enchantment) > 0) {
            enchConsumer.accept(stack.getEnchantmentLevel(enchantment));
        }
    }

    public static void getEnchantmentLevelAndDo(RegistryAccess access, ItemStack stack, ResourceKey<Enchantment> key, Consumer<Integer> enchConsumer) {
        getEnchantmentLevelAndDo(stack, access.registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key), enchConsumer);
    }
}
