package net.kapitencraft.kap_lib.spawn_table.entries;


import com.mojang.serialization.MapCodec;

/**
 * The SerializerType for {@link SpawnPoolEntryContainer}.
 * @param codec the codec of the pool entry
 */
public record SpawnPoolEntryType(MapCodec<? extends SpawnPoolEntryContainer> codec) {
}