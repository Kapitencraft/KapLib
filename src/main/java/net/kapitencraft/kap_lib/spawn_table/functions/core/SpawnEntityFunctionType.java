package net.kapitencraft.kap_lib.spawn_table.functions.core;

import com.mojang.serialization.MapCodec;

/**
 * The SerializerType for {@link SpawnEntityFunction}.
 */
public record SpawnEntityFunctionType<T extends SpawnEntityFunction>(MapCodec<T> codec) {
}