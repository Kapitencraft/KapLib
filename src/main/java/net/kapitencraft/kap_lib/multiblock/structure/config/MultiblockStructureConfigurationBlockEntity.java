package net.kapitencraft.kap_lib.multiblock.structure.config;

import net.kapitencraft.kap_lib.multiblock.registry.MBBlocks;
import net.kapitencraft.kap_lib.multiblock.registry.MBBlockEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class MultiblockStructureConfigurationBlockEntity extends BlockEntity {
    @Nullable
    private ResourceLocation structureName;
    private Vec3i structureSize = Vec3i.ZERO;
    private boolean ignoreEntities = true;
    private Mode mode;

    public MultiblockStructureConfigurationBlockEntity(BlockPos pos, BlockState blockState) {
        super(MBBlockEntityTypes.MULTIBLOCK_STRUCTURE_CONFIG.get(), pos, blockState);
    }

    public Vec3i getStructureSize() {
        return structureSize;
    }

    public void setStructureSize(Vec3i size) {
        this.structureSize = size;
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public boolean hasStructureName() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String structureName) {
        this.setStructureName(StringUtil.isNullOrEmpty(structureName) ? null : ResourceLocation.tryParse(structureName));
    }

    public void setStructureName(@Nullable ResourceLocation structureName) {
        this.structureName = structureName;
    }

    public boolean detectSize() {
        if (this.mode != Mode.SAVE) {
            return false;
        } else {
            BlockPos blockpos = this.getBlockPos();
            int i = 80;
            BlockPos blockpos1 = new BlockPos(blockpos.getX() - i, this.level.getMinBuildHeight(), blockpos.getZ() - i);
            BlockPos blockpos2 = new BlockPos(blockpos.getX() + i, this.level.getMaxBuildHeight() - 1, blockpos.getZ() + i);
            Stream<BlockPos> stream = this.getRelatedCorners(blockpos1, blockpos2);
            return calculateEnclosingBoundingBox(blockpos, stream)
                    .filter(
                            p_155790_ -> {
                                int j = p_155790_.maxX() - p_155790_.minX();
                                int k = p_155790_.maxY() - p_155790_.minY();
                                int l = p_155790_.maxZ() - p_155790_.minZ();
                                if (j > 1 && k > 1 && l > 1) {
                                    this.structureSize = new Vec3i(j - 1, k - 1, l - 1);
                                    this.setChanged();
                                    BlockState blockstate = this.level.getBlockState(blockpos);
                                    this.level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                    )
                    .isPresent();
        }
    }

    private static Optional<BoundingBox> calculateEnclosingBoundingBox(BlockPos pos, Stream<BlockPos> relatedCorners) {
        Iterator<BlockPos> iterator = relatedCorners.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        } else {
            BlockPos blockpos = iterator.next();
            BoundingBox boundingbox = new BoundingBox(blockpos);
            if (iterator.hasNext()) {
                iterator.forEachRemaining(boundingbox::encapsulate);
            } else {
                boundingbox.encapsulate(pos);
            }

            return Optional.of(boundingbox);
        }
    }

    private Stream<BlockPos> getRelatedCorners(BlockPos minPos, BlockPos maxPos) {
        return BlockPos.betweenClosedStream(minPos, maxPos)
                .filter(p_272561_ -> this.level.getBlockState(p_272561_).is(MBBlocks.MULTIBLOCK_STRUCTURE_CONFIG.get()))
                .map(this.level::getBlockEntity)
                .filter(MultiblockStructureConfigurationBlockEntity.class::isInstance)
                .map(MultiblockStructureConfigurationBlockEntity.class::cast)
                .filter(configurationBlockEntity -> configurationBlockEntity.mode == Mode.CORNER && Objects.equals(this.structureName, configurationBlockEntity.structureName))
                .map(BlockEntity::getBlockPos);
    }

    public boolean usedBy(Player player) {
        if (!player.canUseGameMasterBlocks()) {
            return false;
        } else {
            if (player.getCommandSenderWorld().isClientSide) {
                //TODO get to a server-save place
                Minecraft.getInstance().setScreen(new MultiblockStructureConfigurationEditScreen(this));
            }

            return true;
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean saveStructure() {
        return false;
    }

    public enum Mode implements StringRepresentable {
        SAVE,
        CORNER;

        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    public enum UpdateType implements StringRepresentable {
        UPDATE_DATA,
        SAVE_CONFIGURATION,
        SCAN_AREA;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}