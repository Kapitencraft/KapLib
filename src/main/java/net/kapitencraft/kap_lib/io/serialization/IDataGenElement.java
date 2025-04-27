package net.kapitencraft.kap_lib.io.serialization;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @param <T>
 */
public interface IDataGenElement<T extends IDataGenElement<T>> {

    static <T extends IDataGenElement<T>> DataPackSerializer<T> createSerializer(Codec<T> codec, FriendlyByteBuf.Reader<T> factory) {
        return new DataPackSerializer<>(codec, factory, (buf, t) -> t.toNetwork(buf));
    }

    static <T extends IDataGenElement<T>> T fromNetwork(FriendlyByteBuf buf) {
        DataPackSerializer<T> serializer = buf.readRegistryId();
        return serializer.fromNetwork(buf);
    }


    void toNetwork(FriendlyByteBuf buf);

    /**
     * @return the json Object containing the registry id of the serializer and required data for the serializer to work
     * @see ReqCondition#toJson()
     */
    JsonObject toJson();

    DataPackSerializer<T> getSerializer();
}
