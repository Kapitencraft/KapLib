package net.kapitencraft.kap_lib.multiblock.structure.network.C2S;

import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterable;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfigurationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetMultiblockStructureConfigurationBlockDataPacket(BlockPos pos,
                                                                 Vec3i size, String name, MultiblockStructureConfigurationBlockEntity.UpdateType updateType) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetMultiblockStructureConfigurationBlockDataPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetMultiblockStructureConfigurationBlockDataPacket::pos,
            ExtraStreamCodecs.VEC_3I, SetMultiblockStructureConfigurationBlockDataPacket::size,
            ByteBufCodecs.STRING_UTF8, SetMultiblockStructureConfigurationBlockDataPacket::name,
            ExtraStreamCodecs.enumCodec(MultiblockStructureConfigurationBlockEntity.UpdateType.values()), SetMultiblockStructureConfigurationBlockDataPacket::updateType,
            SetMultiblockStructureConfigurationBlockDataPacket::new
    );

    public static final Type<SetMultiblockStructureConfigurationBlockDataPacket> TYPE = new Type<>(LibConstants.res("set_multiblock_structure_configuration"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        Player player = context.player();
        if (player.canUseGameMasterBlocks()) {
            BlockPos blockpos = pos;

            if (player.level().getBlockEntity(blockpos) instanceof MultiblockStructureConfigurationBlockEntity blockEntity) {
                blockEntity.setStructureName(this.name);
                if (blockEntity.hasStructureName()) {
                    String s = blockEntity.getStructureName();
                    switch (updateType) {
                        case SAVE_CONFIGURATION -> {
                            if (blockEntity.saveStructure()) {
                                player.displayClientMessage(Component.translatable("structure_block.save_success", s), false);
                            } else {
                                player.displayClientMessage(Component.translatable("structure_block.save_failure", s), false);
                            }
                        }
                        case SCAN_AREA -> {
                            if (blockEntity.detectSize()) {
                                player.displayClientMessage(Component.translatable("structure_block.size_success", s), false);
                            } else {
                                player.displayClientMessage(Component.translatable("structure_block.size_failure"), false);
                            }
                        }
                    }
                } else {
                    player.displayClientMessage(Component.translatable("structure_block.invalid_structure_name", name), false);
                }                blockEntity.setStructureSize(this.size);
            }
        }
    }
}
