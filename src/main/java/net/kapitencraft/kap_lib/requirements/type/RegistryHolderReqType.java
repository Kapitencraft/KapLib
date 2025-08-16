package net.kapitencraft.kap_lib.requirements.type;

import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public class RegistryHolderReqType<T> implements RequirementType<Holder<T>> {
    private final DataPackSerializer<Holder<T>> serializer;
    private final String name;

    public RegistryHolderReqType(String name, ResourceKey<Registry<T>> resourceKey) {
        this.serializer = new DataPackSerializer<>(
                RegistryFileCodec.create()
        );
        this.name = name;
    }

    @Override
    public @NotNull DataPackSerializer<Holder<T>> serializer() {
        return serializer;
    }

    @Override
    public String getName() {
        return name;
    }
}
