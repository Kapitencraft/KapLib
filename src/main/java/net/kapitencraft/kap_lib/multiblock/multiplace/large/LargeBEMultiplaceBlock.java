package net.kapitencraft.kap_lib.multiblock.multiplace.large;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.MultiblockOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class LargeBEMultiplaceBlock<O extends MultiblockOrientation<O>, P extends MultiplaceBlockPart<P>> extends LargeMultiplaceBlock<O, P> implements EntityBlock {
    public LargeBEMultiplaceBlock(Properties properties, P origin) {
        super(properties, origin);
    }

    @Override
    public final @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (isOrigin(state))
            return createBlockEntity(pos, state);
        return null;
    }

    protected abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);
}
