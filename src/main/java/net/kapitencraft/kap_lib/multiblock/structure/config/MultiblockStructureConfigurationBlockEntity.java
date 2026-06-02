package net.kapitencraft.kap_lib.multiblock.structure.config;

import net.kapitencraft.kap_lib.multiblock.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class MultiblockStructureConfigurationBlockEntity extends BlockEntity {
    @Nullable
    private ResourceLocation structureName;
    private Vec3i structureSize = Vec3i.ZERO;
    private boolean ignoreEntities = true;

    public MultiblockStructureConfigurationBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.MULTIBLOCK_STRUCTURE_CONFIG.get(), pos, blockState);
    }

    public Vec3i getStructureSize() {
        return structureSize;
    }

    public void setStructureSize(Vec3i size) {
        this.structureSize = size;
    }
}
