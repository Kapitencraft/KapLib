package net.kapitencraft.kap_lib.registry.vanilla;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.minecraft.network.chat.contents.BlockDataSource;
import net.minecraft.network.chat.contents.DataSource;
import net.minecraft.network.chat.contents.EntityDataSource;
import net.minecraft.network.chat.contents.StorageDataSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface VanillaDataSourceTypes {

    DeferredRegister<Codec<? extends DataSource>> REGISTRY = DeferredRegister.create(ExtraRegistryKeys.DATA_SOURCE_TYPES, "minecraft");

    RegistryObject<Codec<EntityDataSource>> ENTITY = REGISTRY.register("entity", VanillaDataSourceTypes::createEntity);

    RegistryObject<Codec<StorageDataSource>> STORAGE = REGISTRY.register("storage", VanillaDataSourceTypes::createStorage);

    RegistryObject<Codec<BlockDataSource>> BLOCK = REGISTRY.register("block", VanillaDataSourceTypes::createBlock);

    private static Codec<EntityDataSource> createEntity() {
        return Codec.STRING.xmap(EntityDataSource::new, EntityDataSource::selectorPattern);
    }

    private static Codec<StorageDataSource> createStorage() {
        return ResourceLocation.CODEC.xmap(StorageDataSource::new, StorageDataSource::id);
    }

    private static Codec<BlockDataSource> createBlock() {
        return Codec.STRING.xmap(BlockDataSource::new, BlockDataSource::posPattern);
    }
}
