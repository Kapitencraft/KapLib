package net.kapitencraft.kap_lib.multiblock.structure.config.builder;

import net.kapitencraft.kap_lib.multiblock.registry.MBItemComponentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MultiblockStructureConfiguratorItem extends Item {

    public MultiblockStructureConfiguratorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        BlockPos structureConfigurationLocation = context.getItemInHand().get(MBItemComponentTypes.MB_STRUCTURE_CONFIGURATION_ANCHOR);
        Level level = context.getLevel();
        if (structureConfigurationLocation != null && level.getBlockEntity(structureConfigurationLocation) instanceof MultiblockStructureConfigurationBlockEntity configurationBlockEntity) {
            if (configurationBlockEntity.withinBounds(pos)) {
                configurationBlockEntity.cycleState(pos);
            }
        }
        if (level.getBlockEntity(pos) instanceof MultiblockStructureConfigurationBlockEntity) {
            context.getItemInHand().set(MBItemComponentTypes.MB_STRUCTURE_CONFIGURATION_ANCHOR, pos);
            return InteractionResult.CONSUME;
        }
        return super.useOn(context);
    }
}
