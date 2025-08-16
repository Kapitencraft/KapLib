package net.kapitencraft.kap_lib.io.serialization;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * @param <T>
 */
public interface IDataGenElement<T extends IDataGenElement<T>> {

    static <T extends IDataGenElement<T>> DataPackSerializer<T> createSerializer(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new DataPackSerializer<>(codec, streamCodec);
    }

    /**
     * @return the json Object containing the registry id of the serializer and required data for the serializer to work
     * @see ReqCondition#toJson()
     */
    JsonObject toJson();

    DataPackSerializer<T> getSerializer();
}
