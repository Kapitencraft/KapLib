package net.kapitencraft.kap_lib.multiblock.registry;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfigurationBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public interface MBBlockEntityTypes {
    DeferredRegister<BlockEntityType<?>> REGISTRY = LibConstants.registry(Registries.BLOCK_ENTITY_TYPE);

    Supplier<BlockEntityType<MultiblockStructureConfigurationBlockEntity>> MULTIBLOCK_STRUCTURE_CONFIG =
            REGISTRY.register("multiblock_structure_config", () ->
                    new BlockEntityType<>(
                            MultiblockStructureConfigurationBlockEntity::new,
                            Set.of(MBBlocks.MULTIBLOCK_STRUCTURE_CONFIG.get()),
                            Util.fetchChoiceType(References.BLOCK_ENTITY, "multiblock_structure_config")
                            )
            );
}
