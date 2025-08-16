package net.kapitencraft.kap_lib.io.serialization;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record RegistrySerializer<L>(MapCodec<L> codec, StreamCodec<RegistryFriendlyByteBuf, L> streamCodec) {
}
