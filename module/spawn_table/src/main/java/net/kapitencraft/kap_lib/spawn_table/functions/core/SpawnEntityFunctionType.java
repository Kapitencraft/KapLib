package net.kapitencraft.kap_lib.spawn_table.functions.core;

import com.mojang.serialization.MapCodec;

import java.util.function.Supplier;

/**
 * The SerializerType for {@link SpawnEntityFunction}.
 * @param codec the codec of the function type
 */
public record SpawnEntityFunctionType<T extends SpawnEntityFunction>(Supplier<MapCodec<T>> codec) {
}