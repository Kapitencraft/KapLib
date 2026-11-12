package net.kapitencraft.kap_lib.multiblock.structure.impl;

import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    /**
     * stores the match this BE is part of.
     */
    private Vec3i structureOrigin, structureSize;

    public MultiblockStructureBE(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.isEmpty()) {
            return; //skip to fix error
        }
        this.managerPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, tag.get("Pos")).getOrThrow();

        this.original = IOHelper.orElse(BlockState.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("Original")), Blocks.AIR::defaultBlockState);
        if (this.managerPos.equals(this.worldPosition)) {
            CompoundTag matchTag = tag.getCompound("Match");
            this.structureSize = Vec3i.CODEC.parse(NbtOps.INSTANCE, matchTag.get("Size")).getOrThrow();
            this.structureOrigin = Vec3i.CODEC.parse(NbtOps.INSTANCE, matchTag.get("Origin")).getOrThrow();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Pos", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, this.managerPos).getOrThrow());

        tag.put("Original", IOHelper.orElse(
                BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.original)
                        .ifError(e -> LOGGER.warn("unable to save original state at position {}: {}", this.worldPosition, e.message())),
                CompoundTag::new
        ));

        if (this.managerPos.equals(this.worldPosition)) {
            CompoundTag matchTag = new CompoundTag();
            matchTag.put("Size", Vec3i.CODEC.encodeStart(NbtOps.INSTANCE, this.structureSize).getOrThrow());
            matchTag.put("Origin", Vec3i.CODEC.encodeStart(NbtOps.INSTANCE, this.structureOrigin).getOrThrow());
            //matchTag.putString("Type", this.structureMatch.getConfigId().toString());
            tag.put("Match", matchTag);
        }
    }

    /**
     * provides the manager BE for this BE. this may be itself if the {@link #managerPos} points to this world position
     *
     * @param <T> the class type of this BE
     * @return the manager block entity. delegate to this BE when IO operations are executed (such as item, fluid or energy transfer)
     */
    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    protected @Nullable <T extends MultiblockStructureBE> T getManager() {
        return (T) this.level.getBlockEntity(managerPos);
    }

    /**
     * used to remember the original state to be placed back when the structure is destroyed
     *
     * @param original the original state
     * @apiNote this method should generally be avoided. it can however be used if the BE makes changes to the blocks used in the original structure (i.e. changing logs to coal for a furnace)
     */
    public void setOriginal(BlockState original) {
        this.original = original;
    }

    public BlockState getOriginalState() {
        return this.original;
    }

    /**
     * sets the position of the manager of this multiblock structure
     *
     * @param managerPos the absolute position of the manager (might be current)
     * @apiNote internal. the manager position for any BE should not be changed.
     */
    @ApiStatus.Internal
    public void setManagerPos(BlockPos managerPos) {
        this.managerPos = managerPos;
    }

    @ApiStatus.Internal
    public void setStructureData(Vec3i origin, Vec3i size) {
        this.structureOrigin = origin;
        this.structureSize = size;
    }

    /**
     * used to reset state back to the original
     *
     * @apiNote internal. destruction for multiblock BEs is handled by the API and mustn't be done manually
     */
    @ApiStatus.Internal
    protected void destruct() {
        this.level.setBlockAndUpdate(this.worldPosition, this.original);
        this.level.removeBlockEntity(this.worldPosition);
        //TODO send data
    }

    private boolean removing = false;

    public void onPartRemoved(@NotNull BlockPos removedPos) {
        if (removing)
            return; //skip removing recursively
        this.removing = true;
        Vec3i origin = this.structureOrigin;
        Vec3i size = this.structureSize;
        Level level = this.level;
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    BlockPos pos = new BlockPos(
                            origin.getX() + x,
                            origin.getY() + y,
                            origin.getZ() + z
                    );
                    if (!pos.equals(removedPos) && level.getBlockEntity(pos) instanceof MultiblockStructureBE msBE && msBE.managerPos.equals(this.worldPosition)) {
                        msBE.destruct();
                    }
                }
            }
        }
        this.removing = false;
    }
}