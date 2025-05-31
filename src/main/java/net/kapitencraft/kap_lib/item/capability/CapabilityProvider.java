package net.kapitencraft.kap_lib.item.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * capability provider for item capabilities
 * used inside {@link net.minecraftforge.event.AttachCapabilitiesEvent#addCapability(ResourceLocation, ICapabilityProvider) AttachCapabilities#addCapability}
 * @see <a href="https://docs.minecraftforge.net/en/1.20.x/datastorage/capabilities/">Capability Docs</a>
 */
public abstract class CapabilityProvider<D, C extends AbstractCapability<D>> implements ICapabilitySerializable<Tag> {

    private final C object;
    private final LazyOptional<C> lazy;
    private final Codec<D> codec;
    private final Capability<C> capability;

    /**
     * @param object the object to provide
     * @param codec the codec to serialize this capability
     * @param capability the capability to look for
     */
    protected CapabilityProvider(C object, Codec<D> codec, Capability<C> capability) {
        this.object = object;
        this.lazy = LazyOptional.of(() -> this.object);
        this.codec = codec;
        this.capability = capability;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return this.capability.orEmpty(cap, lazy);
    }

    @Override
    public Tag serializeNBT() {
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, object.getData());
        return result.get().map(Function.identity(), tagPartialResult -> {
            KapLibMod.LOGGER.warn("unable to save capability: {}", tagPartialResult.message());
            return new ListTag();
        });
    }

    @Override
    public void deserializeNBT(Tag t) {
        DataResult<D> result = codec.parse(NbtOps.INSTANCE, t);
        D data = result.get().map(Function.identity(), listPartialResult -> {
            KapLibMod.LOGGER.warn("unable to load capability: {}", listPartialResult.message());
            return fallback();
        });
        object.copyFrom(data);
    }

    /**
     * @return a value to use when deserialization fails
     */
    protected abstract D fallback();
}
