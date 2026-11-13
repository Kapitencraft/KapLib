package net.kapitencraft.kap_lib.multiblock.structure.config.builder;

import net.kapitencraft.kap_lib.multiblock.registry.MBItemComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiblockStructureConfiguratorItem extends Item {

    public MultiblockStructureConfiguratorItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        BlockPos structureConfigurationLocation = context.getItemInHand().get(MBItemComponentTypes.MB_STRUCTURE_CONFIGURATION_ANCHOR);
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player != null) {
            if (player.isShiftKeyDown() && player.level() instanceof ServerLevel sL) {

                if (structureConfigurationLocation != null && level.getBlockEntity(structureConfigurationLocation) instanceof MultiblockStructureConfigurationBlockEntity configurationBlockEntity) {
                    BlockPos structureRelative = pos.subtract(configurationBlockEntity.getStructureOrigin());

                }
            } else {
                if (structureConfigurationLocation != null && level.getBlockEntity(structureConfigurationLocation) instanceof MultiblockStructureConfigurationBlockEntity configurationBlockEntity) {
                    if (configurationBlockEntity.withinBounds(pos)) {
                        configurationBlockEntity.cycleState(pos, player);
                    } else {
                        player.displayClientMessage(Component.translatable("mb.structure.configurator.out_of_bounds"), true);
                    }
                } else if (level.getBlockEntity(pos) instanceof MultiblockStructureConfigurationBlockEntity e && e.getMode() == MultiblockStructureConfigurationBlockEntity.Mode.SAVE) {
                    context.getItemInHand().set(MBItemComponentTypes.MB_STRUCTURE_CONFIGURATION_ANCHOR, pos);
                    player.displayClientMessage(Component.translatable("mb.structure.configurator.select_block", pos.toDimView()), true);
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(Component.translatable("mb.structure.configurator.not_connected"), true);
                    return InteractionResult.SUCCESS_NO_ITEM_USED;
                }
            }
        }
        return super.useOn(context);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        BlockPos pos = stack.get(MBItemComponentTypes.MB_STRUCTURE_CONFIGURATION_ANCHOR);
        if (pos != null && context.level() != null) {
            BlockEntity entity = context.level().getBlockEntity(pos);
            if (entity instanceof MultiblockStructureConfigurationBlockEntity mbsBE) {
                tooltipComponents.add(Component.translatable(
                        "mb.structure.configurator.description",
                        mbsBE.getStructureName(),
                        pos.toDimView()
                ).withStyle(ChatFormatting.GREEN));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
