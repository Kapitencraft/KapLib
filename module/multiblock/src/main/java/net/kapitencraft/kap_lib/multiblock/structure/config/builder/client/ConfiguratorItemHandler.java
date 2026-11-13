package net.kapitencraft.kap_lib.multiblock.structure.config.builder.client;

import net.kapitencraft.kap_lib.multiblock.registry.MBItemComponentTypes;
import net.kapitencraft.kap_lib.multiblock.structure.config.builder.MultiblockStructureConfigurationBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ConfiguratorItemHandler {
    private static MultiblockStructureConfigurationBlockEntity refPoint;

    public static void onKeyInput(int key) {

    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            refPoint = null;
            return;
        }

        BlockPos pos = player.getMainHandItem().get(MBItemComponentTypes.MB_STRUCTURE_CONFIGURATION_ANCHOR);
        if (pos == null) {
            refPoint = null;
            return;
        }
        BlockEntity entity = player.level().getBlockEntity(pos);
        if (entity instanceof MultiblockStructureConfigurationBlockEntity mbsCBE) {
            refPoint = mbsCBE;
        } else
            refPoint = null;
    }

    public static boolean isLookingFor(MultiblockStructureConfigurationBlockEntity blockEntity) {
        return blockEntity == refPoint;
    }
}
