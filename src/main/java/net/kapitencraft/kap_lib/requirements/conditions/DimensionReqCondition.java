package net.kapitencraft.kap_lib.requirements.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DimensionReqCondition extends ReqCondition<DimensionReqCondition> {
    private static final Codec<DimensionReqCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("dimensions").forGetter(i -> i.dimensions),
            Codec.BOOL.fieldOf("inverted").forGetter(i -> i.inverted)
            ).apply(instance, DimensionReqCondition::new)
    );

    public static final DataPackSerializer<DimensionReqCondition> SERIALIZER = new DataPackSerializer<>(
            CODEC, DimensionReqCondition::fromNetwork, DimensionReqCondition::toNetwork
    );

    private final List<ResourceKey<Level>> dimensions;
    private final boolean inverted;

    public DimensionReqCondition(List<ResourceKey<Level>> dimensionKey, boolean inverted) {
        this.dimensions = dimensionKey;
        this.inverted = inverted;
    }

    @SafeVarargs
    public DimensionReqCondition(ResourceKey<Level>... dimensions) {
        this(List.of(dimensions), false);
    }

    @Override
    public @NotNull Component cacheDisplay() {
        String key = "dimension_req.display";
        if (inverted) key += ".inverted";
        List<MutableComponent> dimensionsBaked = dimensions.stream().map(ResourceKey::location).map(ResourceLocation::toString).map(s -> s.replace(':', '.')).map(s -> Component.translatable("dimension." + s)).toList();
        return Component.translatable(key, TextHelper.chain(dimensionsBaked, true));
    }

    public static void toNetwork(FriendlyByteBuf buf, DimensionReqCondition condition) {
        buf.writeCollection(condition.dimensions, FriendlyByteBuf::writeResourceKey);
        buf.writeBoolean(condition.inverted);
    }

    public static DimensionReqCondition fromNetwork(FriendlyByteBuf buf) {
        return new DimensionReqCondition(buf.readList(buf1 -> buf1.readResourceKey(Registries.DIMENSION)), buf.readBoolean());
    }

    @Override
    public boolean matches(LivingEntity entity) {
        return inverted != dimensions.contains(entity.level().dimension());
    }

    @Override
    public DataPackSerializer<DimensionReqCondition> getSerializer() {
        return SERIALIZER;
    }
}
