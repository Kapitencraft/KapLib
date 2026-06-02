package net.kapitencraft.kap_lib.multiblock.structure.network.C2S;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.multiblock.structure.config.MultiblockStructureConfigurationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetMultiblockStructureConfigurationBlockDataPacket(BlockPos pos,
                                                                 Vec3i size) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetMultiblockStructureConfigurationBlockDataPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetMultiblockStructureConfigurationBlockDataPacket::pos,
            ExtraStreamCodecs.VEC_3I, SetMultiblockStructureConfigurationBlockDataPacket::size,
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
                blockEntity.setStructureSize(this.size);

            }
        }
    }
}
