package net.kapitencraft.kap_lib.particle.animation.store;

import com.google.common.collect.Maps;
import net.kapitencraft.kap_lib.core.helpers.CollectionHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleAnimationPresetContext {
    public static final ParticleAnimationPresetContext EMPTY = new ParticleAnimationPresetContext();

    //region object streaming
    private static final StreamCodec<FriendlyByteBuf, Object> DATA_OBJECT_CODEC = StreamCodec.of(
            ParticleAnimationPresetContext::writeDataObject,
            ParticleAnimationPresetContext::readDataObject
    );

    private static void writeDataObject(FriendlyByteBuf buf, Object o) {
        if (o instanceof Vec3 vec3) {
            buf.writeByte(0);
            buf.writeVec3(vec3);
        } else {
            buf.writeByte(1);
            buf.writeUUID((UUID) o);
        }
    }

    private static Object readDataObject(FriendlyByteBuf buf) {
        return switch (buf.readByte()) {
            case 0 -> buf.readVec3();
            case 1 -> buf.readUUID();
            default -> throw new IllegalStateException("unknown object type");
        };
    }
    //endregion
    private static final StreamCodec<FriendlyByteBuf, Map<String, Object>> DATA_MAP_CODEC = ByteBufCodecs.map(CollectionHelper::map, ByteBufCodecs.STRING_UTF8, DATA_OBJECT_CODEC);

    public static final StreamCodec<FriendlyByteBuf, ParticleAnimationPresetContext> STREAM_CODEC = DATA_MAP_CODEC.map(ParticleAnimationPresetContext::create, ParticleAnimationPresetContext::getParams);

    private static ParticleAnimationPresetContext create(Map<String, Object> data) {
        ParticleAnimationPresetContext context = new ParticleAnimationPresetContext();
        context.params.putAll(data);
        return context;
    }

    private Map<String, Object> getParams() {
        return this.params;
    }

    private final Map<String, Object> params = new HashMap<>();

    public void setParam(String name, Vec3 obj) {
        this.params.put(name, obj);
    }

    public void setParam(String name, UUID obj) {
        this.params.put(name, obj);
    }

    public Vec3 getPositionParam(String name) {
        Object param = this.params.get(name);
        if (param == null) {
            throw new IllegalArgumentException("can not find parameter named " + name + " in the context");
        }

        if (param instanceof Vec3 v)
            return v;

        throw new IllegalStateException("parameter named " + name + " is not a position");
    }

    public UUID getEntityParam(String name) {
        Object param = this.params.get(name);
        if (param == null) {
            throw new IllegalArgumentException("can not find parameter named " + name + " in the context");
        }

        if (param instanceof UUID uuid)
            return uuid;

        throw new IllegalStateException("parameter named " + name + " is not a position");
    }
}
