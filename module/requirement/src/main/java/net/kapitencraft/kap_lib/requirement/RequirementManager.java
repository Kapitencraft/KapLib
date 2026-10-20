package net.kapitencraft.kap_lib.requirement;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.core.collection.MapStream;
import net.kapitencraft.kap_lib.core.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.core.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.core.io.JsonHelper;
import net.kapitencraft.kap_lib.requirement.conditions.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.requirement.event.custom.RegisterRequirementTypesEvent;
import net.kapitencraft.kap_lib.requirement.type.RequirementType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class RequirementManager extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LoggerFactory.getLogger("RequirementManager");
    public static RequirementManager instance = new RequirementManager(); //load instantly

    //sync
    private final HashMap<String, Element<?>> elements = new HashMap<>();
    //don't sync
    private final List<RequirementType<?>> types = new ArrayList<>();
    public final StreamCodec<RegistryFriendlyByteBuf, RequirementManager.Data> dataStreamCodec;
    private Map<String, RequirementType<?>> typesByName;

    public RequirementManager() {
        super(JsonHelper.GSON, "requirements");
        registerTypes();
        StreamCodec<RegistryFriendlyByteBuf, Element<?>> elementStreamCodec = StreamCodec.of((buffer, value) -> value.toNetwork(buffer), this::fromNetwork);
        dataStreamCodec = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, elementStreamCodec).map(Data::new, Data::elements);
    }

    @ApiStatus.Internal
    public static void copyData(Data data) {
        instance.elements.clear();
        instance.elements.putAll(data.elements);
    }

    @ApiStatus.Internal
    public static Data createData() {
        return new Data(instance.elements);
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        MapStream.of(pObject)
                .mapKeys(ResourceLocation::getPath)
                .mapKeys(s -> s.replace(".json", ""))
                .mapKeys(typesByName::get)
                .forEach(this::readElement);
    }

    private <T> void readElement(RequirementType<T> type, JsonElement jsonElement) {
        this.elements.values().stream().filter(element -> element.isType(type))
                .findFirst().ifPresentOrElse(element -> element.read(jsonElement), () -> {
                    Element<T> element = new Element<>(type);
                    element.read(jsonElement);
                    this.elements.put(type.getName(), element);
                });
    }

    /**
     * gets all requirements applied to the given value of the given type
     */
    public <T> Collection<ReqCondition<?>> getReqs(RequirementType<T> type, T t) {
        Element<T> element = (Element<T>) this.elements.get(type.getName());
        return element != null ? element.requirements.get(t) : List.of();
    }

    /**
     * add requirement text (e.g. "can only be used in the Nether") to the tooltip given as {@code consumer}
     */
    public static <T> void addReqContent(Consumer<Component> consumer, RequirementType<T> type, T t, @Nullable LivingEntity living) {
        if (instance == null) {
            return;
        }
        List<ReqCondition<?>> reqs = CollectionHelper.mutableList(instance.getReqs(type, t));
        if (living != null) reqs.removeIf(itemRequirement -> itemRequirement.matches(living));
        if (!reqs.isEmpty()) {
            reqs.stream().map(ReqCondition::display)
                    .filter(MutableComponent.class::isInstance)
                    .map(MutableComponent.class::cast)
                    .map(component -> component.withStyle(ChatFormatting.RED))
                    .forEach(consumer);
        }
    }


    /**
     * checks if the given entity matches the given value for the given type
     */
    public <T> boolean meetsRequirements(RequirementType<T> type, @Nullable T value, LivingEntity living) {
        return living != null && getReqs(type, value).stream().allMatch(reqCondition -> reqCondition.matches(living));
    }

    public static boolean meetsItemRequirementsFromEvent(LivingEvent event, EquipmentSlot slot) {
        return instance != null && instance.meetsRequirements(RequirementType.ITEM, event.getEntity().getItemBySlot(slot).getItem(), event.getEntity());
    }

    public static boolean meetsBlockRequirements(Block block, LivingEntity entity) {
        return instance != null && instance.meetsRequirements(RequirementType.BLOCK, block, entity);
    }

    public static boolean meetsBlockRequirementsFromEvent(BlockEvent event, LivingEntity living) {
        return meetsBlockRequirements(event.getState().getBlock(), living);
    }

    private void registerTypes() {
        this.types.add(RequirementType.ITEM);
        this.types.add(RequirementType.BLOCK);
        NeoForge.EVENT_BUS.post(new RegisterRequirementTypesEvent(this.types::add));
        typesByName = this.types.stream().collect(CollectorHelper.toMapForKeys(RequirementType::getName));
    }

    public record Data(HashMap<String, Element<?>> elements) {
    }

    private static class Element<T> {
        private final StreamCodec<RegistryFriendlyByteBuf, Multimap<T, ReqCondition<?>>> reqStreamCodec;

        private final RequirementType<T> type;
        private final Multimap<T, ReqCondition<?>> requirements = HashMultimap.create();

        private Element(RequirementType<T> type) {
            this.type = type;
            this.reqStreamCodec = ExtraStreamCodecs.multimap(type.serializer().getStreamCodec(), ReqCondition.STREAM_CODEC);
        }

        public boolean isType(RequirementType<?> type) {
            return type == this.type;
        }

        public void read(JsonElement jsonElement) {
            try {
                Codec<Map<T, List<ReqCondition<?>>>> codec = Codec.unboundedMap(this.type.serializer().getCodec(), ReqCondition.CODEC.listOf());

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
                LOGGER.warn("error loading requirements for type '{}': {}", this.type.getName(), e.getMessage());
            }
            LOGGER.debug("loaded {} requirements for type {}", this.requirements.size(), this.type.getName());
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
        RequirementType<T> type = (RequirementType<T>) typesByName.get(buf.readUtf());
        Element<T> element = new Element<>(type);
        element.requirements.putAll(element.reqStreamCodec.decode(buf));
        return element;
    }
}
