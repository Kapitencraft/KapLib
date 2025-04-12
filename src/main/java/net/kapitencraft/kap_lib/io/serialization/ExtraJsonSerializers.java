package net.kapitencraft.kap_lib.io.serialization;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public interface ExtraJsonSerializers {
    JsonSerializer<Vec3> VEC_3 = new JsonSerializer<>(Vec3.CODEC, () -> Vec3.ZERO);
    JsonSerializer<BlockPos> BLOCKPOS = new JsonSerializer<>(BlockPos.CODEC, () -> BlockPos.ZERO);
    JsonSerializer<ResourceLocation> RL = new JsonSerializer<>(ResourceLocation.CODEC);
}
