package net.kapitencraft.kap_lib.item.loot_table.modifiers;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.event.custom.ModifyOreDropsEvent;
import net.kapitencraft.kap_lib.helpers.LootTableHelper;
import net.kapitencraft.kap_lib.item.loot_table.IConditional;
import net.kapitencraft.kap_lib.item.loot_table.LootContextReader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

public class OreModifier extends ModLootModifier implements IConditional {
    public static final Codec<OreModifier> CODEC = LootTableHelper.simpleCodec(OreModifier::new);

    protected OreModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }


    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        LootContextReader.simple(context, BlockState.class, LootContextParams.BLOCK_STATE).ifPresent(state -> generatedLoot.forEach(stack -> {
            if (stack.getItem() != state.getBlock().asItem()) {
                ModifyOreDropsEvent event = new ModifyOreDropsEvent(stack.getCount());
                MinecraftForge.EVENT_BUS.post(event);
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
