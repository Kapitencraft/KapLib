package net.kapitencraft.kap_lib.multiblock.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public interface MBItemComponentTypes {
    DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LibConstants.MOD_ID);

    /**
     * defines the location the attaching item will look for the BE of the configuration to use.
     * @apiNote internal. the configurator is only to set up the structures.
     */
    @ApiStatus.Internal
    Supplier<DataComponentType<BlockPos>> MB_STRUCTURE_CONFIGURATION_ANCHOR = REGISTRY.registerComponentType("structure_configuration_anchor", blockPosBuilder ->
            blockPosBuilder.networkSynchronized(BlockPos.STREAM_CODEC).persistent(BlockPos.CODEC)
    );
}
