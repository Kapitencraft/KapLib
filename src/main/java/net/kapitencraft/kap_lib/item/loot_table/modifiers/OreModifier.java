package net.kapitencraft.kap_lib.item.loot_table.modifiers;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.event.custom.ModifyOreDropsEvent;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.helpers.LootTableHelper;
import net.kapitencraft.kap_lib.item.loot_table.IConditional;
import net.kapitencraft.kap_lib.item.loot_table.LootContextReader;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

public class OreModifier extends ModLootModifier implements IConditional {
    public static final Codec<OreModifier> CODEC = LootTableHelper.simpleCodec(OreModifier::new);

    protected OreModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }


    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LootContextReader.simple(context, BlockState.class, LootContextParams.BLOCK_STATE).ifPresent(state -> generatedLoot.forEach(stack -> {
            double attributeValue = AttributeHelper.getSaveAttributeValue(ExtraAttributes.MINING_FORTUNE.get(), LootTableHelper.getLivingSource(context));
            if (stack.getItem() != state.getBlock().asItem()) {
                ModifyOreDropsEvent event = new ModifyOreDropsEvent(stack.getCount() * (int) (1 + attributeValue / 100));
                NeoForge.EVENT_BUS.post(event);
                stack.setCount(event.dropCount.calculate());
            }
        }));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
