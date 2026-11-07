package net.kapitencraft.kap_lib.multiblock.structure.impl;

import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

public class MultiblockStructureBE extends BlockEntity {
    private final static Logger LOGGER = LogUtils.getLogger();

    private BlockState original;
    private BlockPos managerPos;

    public MultiblockStructureBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag pos = tag.getList("Pos", 3);
        this.managerPos = new BlockPos(pos.getInt(0), pos.getInt(1), pos.getInt(2));

        this.original = IOHelper.orElse(BlockState.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("Original")), Blocks.AIR::defaultBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag pos = new ListTag(3);
        pos.add(IntTag.valueOf(this.managerPos.getX()));
        pos.add(IntTag.valueOf(this.managerPos.getY()));
        pos.add(IntTag.valueOf(this.managerPos.getZ()));
        tag.put("Pos", pos);

        tag.put("Original", IOHelper.orElse(
                BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.original)
                        .ifError(e -> LOGGER.warn("unable to save original state at position {}: {}", this.worldPosition, e.message())),
                CompoundTag::new
        ));
    }

    protected <T extends MultiblockStructureBE> T getManager() {
        return (T) this.level.getBlockEntity(managerPos);
    }

    /**
     * used to remember the original state to be placed back when the structure is destroyed
     * @param original the original state
     */
    @ApiStatus.Internal
    public void setOriginal(BlockState original) {
        this.original = original;
    }

    /**
     * used to reset state back to the original
     */
    @ApiStatus.Internal
    protected void destruct() {
        this.level.setBlockAndUpdate(this.worldPosition, this.original);
        //TODO send data
    }
}