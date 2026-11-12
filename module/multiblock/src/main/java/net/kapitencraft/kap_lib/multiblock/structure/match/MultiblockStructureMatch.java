package net.kapitencraft.kap_lib.multiblock.structure.match;

import com.google.common.collect.ImmutableMap;
import net.kapitencraft.kap_lib.multiblock.structure.MultiblockStructurePatternManager;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfiguration;
import net.kapitencraft.kap_lib.multiblock.structure.config.SectionQuantifier;
import net.kapitencraft.kap_lib.multiblock.structure.impl.MultiblockStructureBE;
import net.kapitencraft.kap_lib.multiblock.structure.impl.MultiblockStructureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MultiblockStructureMatch {

    private final BlockState[][][] states;
    private final Map<String, Integer> namedQuantifierSizes;
    private final Vec3i size;
    private final MultiblockStructureConfiguration configuration;
    private final BlockPos origin;

    private MultiblockStructureMatch(BlockState[][][] states, Map<String, Integer> namedQuantifierSizes, Vec3i size, MultiblockStructureConfiguration configuration, BlockPos origin) {
        this.states = states;
        this.namedQuantifierSizes = namedQuantifierSizes;
        this.size = size;
        this.configuration = configuration;
        this.origin = origin;
    }

    @Nullable
    public BlockState getStateAt(BlockPos pos) {
        return states[pos.getX()][pos.getY()][pos.getZ()];
    }

    public BlockState getStateAt(int x, int y, int z) {
        return states[x][y][z];
    }

    public int getQuantifier(String name) {
        return namedQuantifierSizes.get(name);
    }

    public Vec3i getSize() {
        return this.size;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public static MultiblockStructureMatch of(
            Function<BlockPos, BlockState> stateGetter, int[][] permutation,
            MultiblockStructureConfiguration configuration, BlockPos start, BlockPos end
    ) {
        BlockPos size = end.subtract(start);
        BlockState[][][] states = new BlockState[size.getX()][size.getY()][size.getZ()];
        for (int x = 0; x < size.getX(); x++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int z = 0; z < size.getZ(); z++) {
                    if (!configuration.isEmptyAt(permutation, x, y, z)) {
                        states[x][y][z] = stateGetter.apply(start.offset(x, y, z));
                    }
                }
            }
        }

        ImmutableMap.Builder<String, Integer> builder = new ImmutableMap.Builder<>();
        for (Direction.Axis value : Direction.Axis.values()) {
            collectNamedQuantifier(builder, configuration, permutation, value);
        }
        return new MultiblockStructureMatch(states, builder.build(), size, configuration, start);
    }

    private static void collectNamedQuantifier(ImmutableMap.Builder<String, Integer> sink, MultiblockStructureConfiguration configuration, int[][] permutation, Direction.Axis axis) {
        List<SectionQuantifier> sectionQuantifiers = configuration.getQuantifiers(axis);
        for (int i = 0; i < sectionQuantifiers.size(); i++) {
            SectionQuantifier sectionQuantifier = sectionQuantifiers.get(i);
            if (!sectionQuantifier.name().isEmpty()) {
                sink.put(sectionQuantifier.name(), permutation[axis.ordinal()][i]);
            }
        }
    }

    /**
     * replaces the matched section with the block provided in the configuration and initializes the BEs required for the structure to work correctly
     *
     * @param level the level this match is in
     */
    @ApiStatus.Internal
    public void place(LevelAccessor level) {
        MultiblockStructureBlock<?> block = configuration.block();
        BlockState structureState = block.defaultBlockState();

        BlockPos managerPos = null;

        //TODO figure out why last placed block is not updated
        //update: the block is changed before the BE packet arrives.
        //update: the block change packet is cancelled due to server prediction being active
        for (int x = 0; x < this.size.getX(); x++) {
            for (int y = 0; y < this.size.getY(); y++) {
                for (int z = 0; z < this.size.getZ(); z++) {
                    BlockState state = states[x][y][z];
                    if (state != null) {
                        BlockPos sPos = this.origin.offset(x, y, z);
                        int flags = 3; //do update and send to client
                        if (isOutline(x, y, z))
                            flags |= 16; //update neighbours only if necessary //TODO test effectiveness
                        level.setBlock(sPos, structureState, flags);
                        MultiblockStructureBE be = (MultiblockStructureBE) level.getBlockEntity(sPos);
                        if (be == null)
                            throw new NullPointerException("failed to create BE for block at " + sPos.toDimView());
                        if (managerPos == null) {
                            //this will be the manager location. so we can store match data (eg the origin and size)
                            managerPos = sPos;
                            be.setStructureData(this.origin, this.size);
                        }
                        be.setOriginal(state);
                        be.setManagerPos(managerPos);
                    }
                }
            }
        }
    }

    private boolean isOutline(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        for (Direction value : Direction.values()) {
            BlockPos offset = pos.offset(value.getNormal());
            if (offset.within(Vec3i.ZERO, this.size)) {
                if (states[x][y][z] == null)
                    return true;
            }
        }
        return false;
    }

    public ResourceLocation getConfigId() {
        return MultiblockStructurePatternManager.INSTANCE.getKey(this.configuration);
    }
}