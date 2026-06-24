package net.kapitencraft.kap_lib.multiblock.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface MBItemComponentTypes {
    DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LibConstants.MOD_ID);

    Supplier<DataComponentType<BlockPos>> MB_STRUCTURE_CONFIGURATION_ANCHOR = REGISTRY.registerComponentType("structure_configuration_anchor", blockPosBuilder ->
            blockPosBuilder.networkSynchronized(BlockPos.STREAM_CODEC).persistent(BlockPos.CODEC)
    );
}
