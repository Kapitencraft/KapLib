package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.loot.conditions.LootTableTypeCondition;
import net.kapitencraft.kap_lib.loot.conditions.TagKeyCondition;
import net.kapitencraft.kap_lib.loot.modifiers.EnchantmentAddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class TestGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public TestGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        super(output, registries, modid);
    }

    @Override
    protected void start() {
        add("test", new EnchantmentAddItemModifier.Builder()
                .setProvider("ench * 20")
                .setChance(5)
                .setMaxAmount(10)
                .setItem(Items.DIAMOND)
                .setEnchantment(this.registries.holderOrThrow(Enchantments.LOOTING))
                .withCondition(new LootTableTypeCondition(TagKeyCondition.Type.BLOCK))
                .build());
    }
}
