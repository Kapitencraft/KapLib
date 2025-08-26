package net.kapitencraft.kap_lib.requirements.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.CountCondition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * only use when Stat is entry from block mined, item used, -picked up or -crafted as well as entity killed <br>
 * otherwise use {@link CustomStatReqCondition}
 */
public class StatReqCondition extends CountCondition<StatReqCondition> {
    public static ToIntFunction<LivingEntity> createCountExtractor(Stat<?> stat) {
        return value -> !(value instanceof Player) ? -1 :
                value instanceof ServerPlayer sP ?
                        sP.getStats().getValue(stat) :
                        ((LocalPlayer) value).getStats().getValue(stat);
    }

    private static final Map<StatType<?>, Function<?, Component>> STATS_TO_NAME_MAPPER = createStats();

    private static Map<StatType<?>, Function<?, Component>> createStats() {
        Map<StatType<?>, Function<?, Component>> map = new HashMap<>();
        appendStat(map, Stats.BLOCK_MINED, Block::getName);
        appendStat(map, Stats.ITEM_USED, Item::getDescription);
        appendStat(map, Stats.ITEM_PICKED_UP, Item::getDescription);
        appendStat(map, Stats.ITEM_CRAFTED, Item::getDescription);
        appendStat(map, Stats.ENTITY_KILLED, EntityType::getDescription);
        return map;
    }

    private static <T> void appendStat(Map<StatType<?>, Function<?, Component>> map, StatType<T> stat, Function<T, Component> mapper) {
        map.put(stat, mapper);
    }

    private static <T> Component readComponent(Stat<?> stat, StatType<?> type, Function<?, Component> mapper) {
        return createComponent(stat, (StatType<T>) type, (Function<T, Component>) mapper);
    }

    private static <T> Component createComponent(Stat<?> stat, StatType<T> otherType, Function<T, Component> mapper) {
        StatType<?> type = stat.getType();
        if (type == otherType) {
            T t = (T) stat.getValue();
            return mapper.apply(t);
        }
        return null;
    }

    private static <T> String getStatSerializedName(Stat<T> stat) {
        return Stat.buildName(stat.getType(), stat.getValue());
    }

    protected static final Codec<Stat<?>> STAT_CODEC = Codec.STRING.xmap(
            StatReqCondition::getStatFromSerializedName,
            StatReqCondition::getStatSerializedName
    );

    private static <T> Stat<T> getStatFromSerializedName(String statName) {
        String[] split = statName.split(":");
        ResourceLocation statTypeLoc = ResourceLocation.of(split[0], '.');
        StatType<T> type = (StatType<T>) ForgeRegistries.STAT_TYPES.getValue(statTypeLoc);
        if (type == null) throw new IllegalArgumentException("stat type '" + statTypeLoc + "' does not exist");
        ResourceLocation statElementLoc = ResourceLocation.of(split[1], '.');
        T value = type.getRegistry().get(statElementLoc);
        if (value == null) throw new IllegalArgumentException("stat element '" + statElementLoc + "' does not exist");
        return type.get(value);
    }

    private static final Codec<StatReqCondition> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    STAT_CODEC.fieldOf("stat").forGetter(StatReqCondition::getStat),
                    Codec.INT.fieldOf("minCount").forGetter(StatReqCondition::getMinLevel)
            ).apply(instance, StatReqCondition::new)
    );

    public static final DataPackSerializer<StatReqCondition> SERIALIZER = new DataPackSerializer<>(
            CODEC, StatReqCondition::fromNetwork, StatReqCondition::toNetwork
    );

    protected final Stat<?> stat;

    public StatReqCondition(Stat<?> stat, int level) {
        super(createCountExtractor(stat), level);
        this.stat = stat;
    }

    private <T> Component forStatType(Stat<T> stat) {
        StatType<T> type = stat.getType();
        return readComponent(stat, type, STATS_TO_NAME_MAPPER.get(type));
    }

    @Override
    public DataPackSerializer<StatReqCondition> getSerializer() {
        return SERIALIZER;
    }

    private static void toNetwork(FriendlyByteBuf buf, StatReqCondition condition) {
        buf.writeUtf(getStatSerializedName(condition.stat));
        buf.writeInt(condition.minLevel);
    }

    public static StatReqCondition fromNetwork(FriendlyByteBuf buf) {
        return new StatReqCondition(getStatFromSerializedName(buf.readUtf()), buf.readInt());
    }

    private Stat<?> getStat() {
        return stat;
    }

    @Override
    public Component getCountedDisplay() {
        return Component.translatable("stat_req." + getTranslationKey(), minLevel, forStatType(this.stat));
    }

    private String getTranslationKey() {
        return Objects.requireNonNull(ForgeRegistries.STAT_TYPES.getKey(this.stat.getType()), "unknown stat type: " + this.stat.getType().getClass().getCanonicalName()).toString().replace(':', '.');
    }
}