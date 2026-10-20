package net.kapitencraft.kap_lib.multiblock.multiplace.line;

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

public abstract class Multiplace2x1EntityBlock<B extends BlockEntity> extends Multiplace2x1Block implements EntityBlock {
    public Multiplace2x1EntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public final @Nullable B newBlockEntity(BlockPos pos, BlockState state) {
        if (isOrigin(state))
            return createBlockEntity(pos, state);
        return null;
    }

    protected abstract B createBlockEntity(BlockPos pos, BlockState state);

    /**
     * override if the {@code useWithoutItem} method, providing access to the BE of the multiplace block.
     * do note that the state is not the state of the origin but clicked
     *
     * @param state  the state of the clicked block
     * @param level  the level of the clicked block
     * @param pos    the position of the clicked block
     * @param player the player clicking the block
     * @param result the hit result
     * @param entity the block entity of the clicked multiplace
     * @return an interaction result
     */
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
