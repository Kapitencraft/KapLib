package net.kapitencraft.kap_lib.loot.modifiers;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kapitencraft.kap_lib.loot.event.custom.ModifyOreDropsEvent;
import net.kapitencraft.kap_lib.loot.IConditional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

public class OreModifier extends ModLootModifier implements IConditional {
    public static final MapCodec<OreModifier> CODEC = IConditional.simpleCodec(OreModifier::new);

    protected OreModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
        if (state != null) {
            Entity param = context.getParam(LootContextParams.THIS_ENTITY);
            if (!(param instanceof LivingEntity)) return generatedLoot;
            generatedLoot.forEach(stack -> {
                if (stack.getItem() != state.getBlock().asItem()) {
                    ModifyOreDropsEvent event = new ModifyOreDropsEvent(stack.getCount());
                    NeoForge.EVENT_BUS.post(event);
                    stack.setCount(event.dropCount.calculate());
                }
            });
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
