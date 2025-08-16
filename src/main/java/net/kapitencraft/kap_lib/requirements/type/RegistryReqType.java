package net.kapitencraft.kap_lib.requirements.type;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public class RegistryReqType<T> implements RequirementType<T> {

    private final DataPackSerializer<T> serializer;
    private final String name;

    public static <T> RegistryReqType<T> registry(String name, Registry<T> registry, ResourceKey<Registry<T>> resourceKey) {
        return new RegistryReqType<>(name, registry.byNameCodec(), resourceKey);
    }

    public static <T> RegistryHolderReqType<T> registryHolder(String name, ResourceKey<Registry<T>> resourceKey) {
        return new RegistryHolderReqType<>(name, resourceKey);
    }

    public RegistryReqType(String name, Codec<T> codec, ResourceKey<Registry<T>> registry) {
        serializer = new DataPackSerializer<>(
                codec,
                ByteBufCodecs.registry(registry)
        );
        this.name = name;
    }

    @Override
    public @NotNull DataPackSerializer<T> serializer() {
        return serializer;
    }

    public String getName() {
        return name;
    }
}