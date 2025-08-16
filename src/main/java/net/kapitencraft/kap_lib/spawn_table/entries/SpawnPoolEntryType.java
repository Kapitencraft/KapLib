package net.kapitencraft.kap_lib.spawn_table.entries;


import com.mojang.serialization.MapCodec;

/**
 * The SerializerType for {@link SpawnPoolEntryContainer}.
 */
public record SpawnPoolEntryType(MapCodec<? extends SpawnPoolEntryContainer> codec) {
}