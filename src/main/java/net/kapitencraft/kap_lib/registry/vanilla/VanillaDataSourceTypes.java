package net.kapitencraft.kap_lib.registry.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.contents.BlockDataSource;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.StorageDataSource;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface VanillaDataSourceTypes {

    DeferredRegister<MapCodec<? extends DataSource>> REGISTRY = DeferredRegister.create(ExtraRegistries.Keys.DATA_SOURCE_TYPES, "minecraft");

    Supplier<MapCodec<EntityDataSource>> ENTITY = REGISTRY.register("entity", VanillaDataSourceTypes::createEntity);

    Supplier<MapCodec<StorageDataSource>> STORAGE = REGISTRY.register("storage", VanillaDataSourceTypes::createStorage);

    Supplier<MapCodec<BlockDataSource>> BLOCK = REGISTRY.register("block", VanillaDataSourceTypes::createBlock);

    private static MapCodec<EntityDataSource> createEntity() {
        return Codec.STRING.xmap(EntityDataSource::new, EntityDataSource::selectorPattern).fieldOf("entity");
    }

    private static MapCodec<StorageDataSource> createStorage() {
        return ResourceLocation.CODEC.xmap(StorageDataSource::new, StorageDataSource::id).fieldOf("storage");
    }

    private static MapCodec<BlockDataSource> createBlock() {
        return Codec.STRING.xmap(BlockDataSource::new, BlockDataSource::posPattern).fieldOf("block");
    }
}
