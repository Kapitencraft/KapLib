package net.kapitencraft.kap_lib.multiblock.structure.config.builder;

import net.kapitencraft.kap_lib.multiblock.registry.MBBlocks;
import net.kapitencraft.kap_lib.multiblock.registry.MBBlockEntityTypes;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class MultiblockStructureConfigurationBlockEntity extends BlockEntity {
    @Nullable
    private ResourceLocation structureName;
    private BlockPos structurePos = new BlockPos(0, 1, 0);
    private Vec3i structureSize = Vec3i.ZERO;
    private boolean ignoreEntities = true;
    private Mode mode = Mode.SAVE;
    private final Map<BlockPos, MultiblockStructureConfiguration.BlockInstance> instances = new HashMap<>();
    private final List<String> groups = new ArrayList<>();

    public MultiblockStructureConfigurationBlockEntity(BlockPos pos, BlockState blockState) {
        super(MBBlockEntityTypes.MULTIBLOCK_STRUCTURE_CONFIG.get(), pos, blockState);
    }

    public Vec3i getStructureSize() {
        return structureSize;
    }

    public void setStructureSize(Vec3i size) {
        this.structureSize = size;
    }

    public BlockPos getStructurePos() {
        return this.structurePos;
    }

    public void setStructurePos(BlockPos structurePos) {
        this.structurePos = structurePos;
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
                                    this.structurePos = new BlockPos(
                                            p_155790_.minX() - blockpos.getX() + 1, p_155790_.minY() - blockpos.getY() + 1, p_155790_.minZ() - blockpos.getZ() + 1
                                    );
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

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("name", this.getStructureName());
        tag.putInt("sizeX", this.structureSize.getX());
        tag.putInt("sizeY", this.structureSize.getY());
        tag.putInt("sizeZ", this.structureSize.getZ());
        tag.putInt("posX", this.structurePos.getX());
        tag.putInt("posY", this.structurePos.getY());
        tag.putInt("posZ", this.structurePos.getZ());
        tag.putString("mode", this.mode.toString());
        tag.putBoolean("ignoreEntities", this.ignoreEntities);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.setStructureName(tag.getString("name"));
        int i = Mth.clamp(tag.getInt("posX"), -48, 48);
        int j = Mth.clamp(tag.getInt("posY"), -48, 48);
        int k = Mth.clamp(tag.getInt("posZ"), -48, 48);
        this.structurePos = new BlockPos(i, j, k);
        int l = Mth.clamp(tag.getInt("sizeX"), 0, 48);
        int i1 = Mth.clamp(tag.getInt("sizeY"), 0, 48);
        int j1 = Mth.clamp(tag.getInt("sizeZ"), 0, 48);
        this.structureSize = new Vec3i(l, i1, j1);

        try {
            this.mode = Mode.valueOf(tag.getString("mode"));
        } catch (IllegalArgumentException illegalargumentexception) {
            this.mode = Mode.SAVE;
        }

        this.ignoreEntities = tag.getBoolean("ignoreEntities");
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

    public Mode getMode() {
        return this.mode;
    }

    public boolean withinBounds(BlockPos pos) {
        BlockPos structureOrigin = this.getBlockPos().offset(this.structurePos);
        BlockPos end = structureOrigin.offset(this.structureSize);
        return pos.getX() >= structureOrigin.getX() && pos.getY() <= end.getX() &&
                pos.getY() >= structureOrigin.getY() && pos.getY() <= end.getY() &&
                pos.getZ() >= structureOrigin.getZ() && pos.getZ() <= end.getZ();
    }

    public void cycleState(BlockPos pos) {
        BlockPos structureOrigin = this.getBlockPos().offset(this.structurePos);
        BlockPos relative = pos.subtract(structureOrigin);
        MultiblockStructureConfiguration.BlockInstance instance = this.instances.get(relative);
        BlockState state = this.level.getBlockState(pos);
        List<TagKey<Block>> list = state.getBlockHolder().tags().toList();
        if (instance == null) {
            this.instances.put(relative, MultiblockStructureConfiguration.BlockInstance.forTag(list.getFirst()));
        } else
            instance = instance.cycle(state, list, this.groups);
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