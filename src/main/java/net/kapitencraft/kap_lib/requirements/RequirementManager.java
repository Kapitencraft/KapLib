package net.kapitencraft.kap_lib.requirements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.event.custom.RegisterRequirementTypesEvent;
import net.kapitencraft.kap_lib.helpers.CollectorHelper;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.requirements.type.BonusRequirementType;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.kapitencraft.kap_lib.requirements.type.RequirementType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RequirementManager extends SimpleJsonResourceReloadListener {
    public static RequirementManager instance;

    //sync
    private final Map<String, Element<?>> elements = new HashMap<>();
    //don't sync
    private final List<RequirementType<?>> types = new ArrayList<>();
    private Map<String, RequirementType<?>> typesForNames;

    public RequirementManager() {
        super(JsonHelper.GSON, "requirements");
        registerTypes();
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
        return instance.meetsRequirements(RegistryReqType.ITEM, event.getEntity().getItemBySlot(slot).getItem(), event.getEntity());
    }

    private void registerTypes() {
        this.types.add(RequirementType.ITEM);
        this.types.add(RequirementType.ENCHANTMENT);
        this.types.add(RequirementType.BONUS);
        MinecraftForge.EVENT_BUS.post(new RegisterRequirementTypesEvent(this.types::add));
        typesForNames = this.types.stream().collect(CollectorHelper.createMapForKeys(RequirementType::getName));
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeMap(this.elements, FriendlyByteBuf::writeUtf, (buf1, element) -> element.toNetwork(buf1));
    }

    public void readFromNetwork(FriendlyByteBuf buf) {
        this.elements.putAll(buf.readMap(FriendlyByteBuf::readUtf, this::fromNetwork));
    }


    private static class Element<T> {
        private final RequirementType<T> type;
        private final Multimap<T, ReqCondition<?>> requirements = HashMultimap.create();

        private Element(RequirementType<T> type) {
            this.type = type;
        }

        public boolean isType(RequirementType<?> type) {
            return type == this.type;
        }

        public void read(JsonElement jsonElement) {
            try {
                JsonObject object = jsonElement.getAsJsonObject();
                MapStream.of(object.asMap())
                        .mapKeys(ResourceLocation::new)
                        .mapKeys(this.type::getById)
                        .filterKeys(Objects::nonNull)
                        .mapValues(JsonElement::getAsJsonObject)
                        .mapValues(ReqCondition::readFromJson)
                        .forEach(this::addElement);
            } catch (Exception e) {
                KapLibMod.LOGGER.warn(Markers.REQUIREMENTS_MANAGER, "error loading requirements for type '{}': {}", this.type.getName(), e.getMessage());
            }
        }

        private void addElement(T value, ReqCondition<?> condition) {
            if (condition != null) this.requirements.put(value, condition);
        }

        private void toNetwork(FriendlyByteBuf buf) {
            buf.writeUtf(this.type.getName());
                buf.writeMap(requirements.asMap(),
                    (buf1, t) -> buf1.writeResourceLocation(this.type.getId(t)),
                    (buf1, reqConditions) -> buf1.writeCollection(reqConditions, (byteBuf, reqCondition) -> reqCondition.toNetwork(byteBuf))
            );
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Element<T> fromNetwork(FriendlyByteBuf buf) {
        RequirementType<T> type = (RequirementType<T>) typesForNames.get(buf.readUtf());
        Element<T> element = new Element<>(type);
        Map<T, List<ReqCondition<?>>> map = buf.readMap(
                buf1 -> type.getById(buf1.readResourceLocation()),
                buf1 -> buf1.readList(ReqCondition::fromNetwork)
        );
        map.forEach((t, reqConditions) -> reqConditions.forEach(reqCondition -> element.addElement(t, reqCondition)));
        return element;
    }
}
