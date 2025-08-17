package net.kapitencraft.kap_lib.requirements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.event.custom.RegisterRequirementTypesEvent;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.io.network.S2C.SyncRequirementsPacket;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.requirements.type.RegistryHolderReqType;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.kapitencraft.kap_lib.requirements.type.RequirementType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import org.antlr.v4.runtime.misc.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class RequirementManager extends SimpleJsonResourceReloadListener {
    public static final  Logger LOGGER = LogUtils.getLogger();
    public static RequirementManager instance = new RequirementManager(); //load instantly

    //sync
    private final HashMap<String, Element<?>> elements = new HashMap<>();
    //don't sync
    private final List<RequirementType<?>> types = new ArrayList<>();
    public final StreamCodec<RegistryFriendlyByteBuf, RequirementManager.Data> dataStreamCodec;
    private Map<String, RequirementType<?>> typesForNames;

    public RequirementManager() {
        super(JsonHelper.GSON, "requirements");
        registerTypes();
        StreamCodec<RegistryFriendlyByteBuf, Element<?>> elementStreamCodec = StreamCodec.of((buffer, value) -> value.toNetwork(buffer), this::fromNetwork);
        dataStreamCodec = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, elementStreamCodec).map(Data::new, Data::elements);
    }

    public static void copyData(Data data) {
        instance.elements.clear();
        instance.elements.putAll(data.elements);
    }

    public static Data createData() {
        return new Data(instance.elements);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        MapStream.of(pObject)
                .mapKeys(ResourceLocation::getPath)
                .mapKeys(s -> s.replace(".json", ""))
                .mapKeys(typesForNames::get)
                .forEach(this::readElement);
    }

    private <T> void readElement(RequirementType<T> type, JsonElement jsonElement) {
        this.elements.values().stream().filter(element -> element.isType(type))
                .findFirst().ifPresentOrElse(element -> element.read(jsonElement), ()-> {
                    Element<T> element = new Element<>(type);
                    element.read(jsonElement);
                    this.elements.put(type.getName(), element);
                });
    }

    /**
     * gets all requirements applied to the given value of the given type
     */
    @SuppressWarnings("unchecked")
    public <T> Collection<ReqCondition<?>> getReqs(RequirementType<T> type, T t) {
        Element<T> element = (Element<T>) this.elements.get(type.getName());
        return element != null ? element.requirements.get(t) : List.of();
    }

    /**
     * checks if the given entity matches the given value for the given type
     */
    public <T> boolean meetsRequirements(RequirementType<T> type, @Nullable T value, LivingEntity living) {
        return living != null && getReqs(type, value).stream().allMatch(reqCondition -> reqCondition.matches(living));
    }

    public static boolean meetsItemRequirementsFromEvent(LivingEvent event, EquipmentSlot slot) {
        return instance != null && instance.meetsRequirements(RegistryReqType.ITEM, event.getEntity().getItemBySlot(slot).getItem(), event.getEntity());
    }

    private void registerTypes() {
        this.types.add(RequirementType.ITEM);
        this.types.add(RequirementType.ENCHANTMENT);
        this.types.add(RequirementType.BONUS);
        NeoForge.EVENT_BUS.post(new RegisterRequirementTypesEvent(this.types::add));
        typesForNames = this.types.stream().collect(CollectorHelper.createMapForKeys(RequirementType::getName));
    }

    public record Data(HashMap<String, Element<?>> elements) {
    }

    private static class Element<T> {
        private final StreamCodec<RegistryFriendlyByteBuf, Multimap<T, ReqCondition<?>>> reqStreamCodec;

        private final RequirementType<T> type;
        private final Multimap<T, ReqCondition<?>> requirements = HashMultimap.create();

        private Element(RequirementType<T> type) {
            this.type = type;
            this.reqStreamCodec = ExtraStreamCodecs.multimap(this.type.serializer().getStreamCodec(), ReqCondition.STREAM_CODEC);
        }

        public boolean isType(RequirementType<?> type) {
            return type == this.type;
        }

        public void read(JsonElement jsonElement) {
            try {
                Codec<Map<T, List<ReqCondition<?>>>> codec = Codec.unboundedMap(this.type.serializer().getCodec(),  ReqCondition.CODEC.listOf());

                DataResult<Map<T, List<ReqCondition<?>>>> result = codec.parse(JsonOps.INSTANCE, jsonElement);
                result.resultOrPartial(s -> LOGGER.warn("error loading requirements for type: {}", s))
                        .ifPresent(m ->
                                m.forEach((t, reqConditions) ->
                                        reqConditions.forEach(c ->
                                                this.addElement(t, c)
                                        )
                                )
                        );
            } catch (Exception e) {
                LOGGER.warn(Markers.REQUIREMENTS_MANAGER, "error loading requirements for type '{}': {}", this.type.getName(), e.getMessage());
            }
        }

        private void addElement(T value, ReqCondition<?> condition) {
            if (condition != null) this.requirements.put(value, condition);
        }

        private void toNetwork(RegistryFriendlyByteBuf buf) {
            buf.writeUtf(this.type.getName());
            this.reqStreamCodec.encode(buf, requirements);
        }
    }

    private <T> Element<T> fromNetwork(RegistryFriendlyByteBuf buf) {
        RequirementType<T> type = (RequirementType<T>) typesForNames.get(buf.readUtf());
        Element<T> element = new Element<>(type);
        element.requirements.putAll(element.reqStreamCodec.decode(buf));
        return element;
    }
}
