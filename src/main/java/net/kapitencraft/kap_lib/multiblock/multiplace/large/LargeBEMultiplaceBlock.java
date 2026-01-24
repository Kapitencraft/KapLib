package net.kapitencraft.kap_lib.multiblock.multiplace.large;

import net.kapitencraft.kap_lib.multiblock.multiplace.large.orientation.MultiblockOrientation;
import net.kapitencraft.kap_lib.multiblock.multiplace.large.part.MultiplaceBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public abstract class LargeBEMultiplaceBlock<O extends MultiblockOrientation<O>, P extends MultiplaceBlockPart<P>, B extends BlockEntity> extends LargeMultiplaceBlock<O, P> implements EntityBlock {
    public LargeBEMultiplaceBlock(Properties properties, P origin) {
        super(properties, origin);
    }

    @Override
    public final @Nullable B newBlockEntity(BlockPos pos, BlockState state) {
        if (isOrigin(state))
            return createBlockEntity(pos, state);
        return null;
    }

    protected abstract B createBlockEntity(BlockPos pos, BlockState state);

    protected InteractionResult playerUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult result, B entity) {
        if (entity instanceof MenuProvider menuProvider) {
            if (level.isClientSide()) return InteractionResult.SUCCESS;
            player.openMenu(menuProvider);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected final InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos origin = getOriginPositionFromState(state, pos);
        return playerUse(state, level, pos, player, hitResult, (B) level.getBlockEntity(origin));
    }
}
