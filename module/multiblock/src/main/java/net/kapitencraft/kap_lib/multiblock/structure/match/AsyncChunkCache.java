package net.kapitencraft.kap_lib.multiblock.structure.match;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;

/**
 * stores the required level chunk sections during async pattern matching in order to prevent
 * deadlock from vanilla code requirement to execute chunk related code on the server thread.
 */
public class AsyncChunkCache {
    private final int originX, originY, originZ;
    private final LevelChunkSection[][][] sections;

    private AsyncChunkCache(int originX, int originY, int originZ, LevelChunkSection[][][] sections) {
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
        this.sections = sections;
    }

    /**
     * creates a new AsyncChunkCache for use in Async tasks.
     *
     * @param accessor the world to fetch the chunk sections from
     * @param origin   the origin position of the fetch
     * @param size     the size of the fetch, in world position grid, not section grid
     * @return the created cache for use in async tasks
     * @apiNote this method must be executed on the main thread, to ensure sections can be gotten
     */
    public static AsyncChunkCache create(LevelAccessor accessor, BlockPos origin, BlockPos size) {
        int cStartX = origin.getX() >> 4;
        int cStartY = origin.getY() >> 4;
        int cStartZ = origin.getZ() >> 4;
        int cEndX = (origin.getX() + size.getX()) >> 4;
        int cEndY = (origin.getY() + size.getY()) >> 4;
        int cEndZ = (origin.getZ() + size.getZ()) >> 4;
        LevelChunkSection[][][] sections = new LevelChunkSection[cEndX - cStartX + 1][cEndY - cStartY + 1][cEndZ - cStartZ + 1];
        for (int x = cStartX; x <= cEndX; x++) {
            for (int y = cStartY; y <= cEndY; y++) {
                for (int z = cStartZ; z <= cEndZ; z++) {
                    ChunkAccess access = accessor.getChunk(x, z);
                    sections[x - cStartX][y - cStartY][z - cStartZ] =
                            access.getSection(access.getSectionIndexFromSectionY(y));
                }
            }
        }
        return new AsyncChunkCache(origin.getX(), origin.getY(), origin.getZ(), sections);
    }

    public @NotNull BlockState getBlockState(BlockPos pos) {

        LevelChunkSection access = sections //get relative section
                [(pos.getX() >> 4) - (originX >> 4)]
                [(pos.getY() >> 4) - (originY >> 4)]
                [(pos.getZ() >> 4) - (originZ >> 4)];
        //get relative block in section
        return access.getBlockState(
                pos.getX() & 15,
                pos.getY() & 15,
                pos.getZ() & 15
        );
    }
}
