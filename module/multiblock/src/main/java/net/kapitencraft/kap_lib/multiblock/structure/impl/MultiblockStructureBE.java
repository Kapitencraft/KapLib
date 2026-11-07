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

    /**
     * stores the BlockState that was placed in the world position before the structure formed.
     * the structure ensures to reset the world back to the old state when the structure gets destroyed
     */
    private BlockState original;
    /**
     * the in-world (absolute) position of the manager BE of this multiblock structure.
     * all logic should be handled on the manager and not on any other of the BEs as this may cause desync
     */
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

    /**
     * provides the manager BE for this BE. this may be itself if the {@link #managerPos} points to this world position
     * @param <T> the class type of this BE
     * @return the manager block entity. delegate to this BE when IO operations are executed (such as item, fluid or energy transfer)
     */
    protected <T extends MultiblockStructureBE> T getManager() {
        return (T) this.level.getBlockEntity(managerPos);
    }

    /**
     * used to remember the original state to be placed back when the structure is destroyed
     * @param original the original state
     * @apiNote this method should generally be avoided. it can however be used if the BE makes changes to the blocks used in the original structure (i.e. changing logs to coal for a furnace)
     */
    public void setOriginal(BlockState original) {
        this.original = original;
    }

    /**
     * sets the position of the manager of this multiblock structure
     * @param managerPos the absolute position of the manager (might be current)
     * @apiNote internal. the manager position for any BE should not be changed.
     */
    @ApiStatus.Internal
    public void setManagerPos(BlockPos managerPos) {
        this.managerPos = managerPos;
    }

    /**
     * used to reset state back to the original
     * @apiNote internal. destruction for multiblock BEs is handled by the API and mustn't be done manually
     */
    @ApiStatus.Internal
    protected void destruct() {
        this.level.setBlockAndUpdate(this.worldPosition, this.original);
        //TODO send data
    }
}