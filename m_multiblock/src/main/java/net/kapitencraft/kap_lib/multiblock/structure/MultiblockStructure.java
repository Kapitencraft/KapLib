package net.kapitencraft.kap_lib.multiblock.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record MultiblockStructure(List<BlockInfo> infos) {
    public static final Codec<MultiblockStructure> CODEC = BlockInfo.CODEC.listOf().xmap(MultiblockStructure::new, MultiblockStructure::infos);

    private record BlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
        private static final Codec<BlockInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(BlockInfo::pos),
                BlockState.CODEC.fieldOf("state").forGetter(BlockInfo::state),
                CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(in -> Optional.ofNullable(in.nbt))
        ).apply(i, BlockInfo::fromCodec));

        private static BlockInfo fromCodec(BlockPos pos, BlockState state, Optional<CompoundTag> compoundTag) {
            return new BlockInfo(pos, state, compoundTag.orElse(null));
        }
    }

    public record Placed(ResourceLocation structure) {
        public static final Codec<Placed> CODEC = RecordCodecBuilder.create(i -> i.group(
                ResourceLocation.CODEC.fieldOf("structure").forGetter(Placed::structure)
        ).apply(i, Placed::new));
    }
}
