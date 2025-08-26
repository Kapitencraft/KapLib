package net.kapitencraft.kap_lib.helpers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.K;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

public class NetworkHelper {

    public static void writeVec3(FriendlyByteBuf buf, Vec3 vec3) {
        buf.writeDouble(vec3.x);
        buf.writeDouble(vec3.y);
        buf.writeDouble(vec3.z);
    }

    public static void writeVec2(FriendlyByteBuf buf, Vec2 value) {
        buf.writeFloat(value.x);
        buf.writeFloat(value.y);
    }

    public static <T> void writeArray(FriendlyByteBuf buf, T[] array, FriendlyByteBuf.Writer<T> writer) {
        buf.writeInt(array.length);
        for (T t : array) {
            writer.accept(buf, t);
        }
    }

    public static <T> T[] readArray(FriendlyByteBuf buf, IntFunction<T[]> constructor, FriendlyByteBuf.Reader<T> reader) {
        int length = buf.readInt();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(reader.apply(buf));
        }
        return list.toArray(constructor);
    }

    public static Vec3 readVec3(FriendlyByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static Vec2 readVec2(FriendlyByteBuf buf) {
        return new Vec2(buf.readFloat(), buf.readFloat());
    }


    public static <T extends ParticleOptions> T readParticleOptions(FriendlyByteBuf buf) {
        ParticleType<T> type = (ParticleType<T>) buf.readById(BuiltInRegistries.PARTICLE_TYPE);
        return Objects.requireNonNull(type).getDeserializer().fromNetwork(type, buf);
    }

    public static void writeParticleOptions(FriendlyByteBuf buf, ParticleOptions toSpawn) {
        buf.writeId(BuiltInRegistries.PARTICLE_TYPE, toSpawn.getType());
        toSpawn.writeToNetwork(buf);
    }


    public static void writeVector3f(FriendlyByteBuf buf, Vector3f vec) {
        buf.writeFloat(vec.x);
        buf.writeFloat(vec.y);
        buf.writeFloat(vec.z);
    }

    public static Entity entityFromNw(FriendlyByteBuf buf) {
        return ClientHelper.getEntity(buf.readInt());
    }

    public static <K, V> void writeMultimap(FriendlyByteBuf buf, Multimap<K, V> multimap, FriendlyByteBuf.Writer<K> keyWriter, FriendlyByteBuf.Writer<V> valueWriter) {
        buf.writeMap(CollectionHelper.fromMultimap(multimap), keyWriter, (buf1, vs) -> buf1.writeCollection(vs, valueWriter));
    }

    public static <K, V> Multimap<K, V> readMultimap(FriendlyByteBuf buf, FriendlyByteBuf.Reader<K> keyReader, FriendlyByteBuf.Reader<V> valueReader) {
        return CollectionHelper.fromListMap(buf.readMap(keyReader, buf1 -> buf1.readCollection(ArrayList::new, valueReader)));
    }
}
