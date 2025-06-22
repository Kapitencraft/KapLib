package net.kapitencraft.kap_lib.item.bonus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.collection.DoubleMap;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.event.custom.RegisterBonusProvidersEvent;
import net.kapitencraft.kap_lib.event.custom.WearableSlotChangeEvent;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.InventoryHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.inventory.wearable.WearableSlot;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.S2C.UpdateBonusDataPacket;
import net.kapitencraft.kap_lib.registry.ExtraCodecs;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.requirements.type.RequirementType;
import net.kapitencraft.kap_lib.util.Color;
import net.kapitencraft.kap_lib.util.Reference;
import net.kapitencraft.kap_lib.util.Vec2i;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class BonusManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static BonusManager instance;
    public static BonusManager updateInstance() {
        if (instance != null) MinecraftForge.EVENT_BUS.unregister(instance);
        return instance = new BonusManager();
    }

    public static void swapFrom(LivingEntity living, EquipmentSlot slot, ItemStack newItem, ItemStack oldItem) {
        instance.getOrCreateLookup(living).equipmentChange(slot, oldItem, newItem);
    }

    private Optional<BonusLookup> getLookup(LivingEntity living) {
        if (lookupMap.containsKey(living)) return Optional.of(lookupMap.get(living));
        return Optional.empty();
    }

    private BonusLookup getOrCreateLookup(LivingEntity living) {
        return lookupMap.computeIfAbsent(living, BonusLookup::new);
    }

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        BonusLookup bonusLookup = getOrCreateLookup(entity);
        bonusLookup.equipmentChange(event.getSlot(), event.getFrom(), event.getTo());
        if (entity instanceof ServerPlayer sP) ModMessages.sendToClientPlayer(new UpdateBonusDataPacket(event.getFrom(), event.getTo(), event.getSlot(), entity.getId()), sP);
    }

    @SubscribeEvent
    public void onWearableSlotChange(WearableSlotChangeEvent event) {
        LivingEntity entity = event.getEntity();
        BonusLookup bonusLookup = getOrCreateLookup(entity);
        bonusLookup.wearableChange(event.getSlot(), event.getFrom(), event.getTo());
    }


    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return; //ONLY SERVERSIDE
        getLookup(event.getEntity()).ifPresent(BonusLookup::tick); //only tick when necessary
    }

    private final Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers;

    private final Map<ResourceLocation, SetBonusElement> sets = new HashMap<>();
    private final Map<ResourceLocation, BonusElement> bonusData = new HashMap<>();
    private final DoubleMap<Item, ResourceLocation, BonusElement> itemBonuses = DoubleMap.create();
    private final Map<LivingEntity, BonusLookup> lookupMap = new HashMap<>();

    public BonusElement getSet(ResourceLocation location) {
        return Objects.requireNonNull(sets.get(location), "unknown set bonus: '" + location + "'");
    }

    public BonusElement getItemBonus(ResourceLocation location) {
        return Objects.requireNonNull(bonusData.get(location), "unknown item bonus: '" + location + "'");
    }

    public List<AbstractBonusElement> getAllActive(LivingEntity living) {
        return this.getLookup(living).map(BonusLookup::allActive).orElse(ImmutableList.of());
    }

    private Map<ResourceLocation, SetBonusElement> getActiveSetBonuses(LivingEntity living) {
        Map<EquipmentSlot, ItemStack> equipment = InventoryHelper.equipment(living);
        Map<ResourceLocation, SetBonusElement> bonuses = new HashMap<>();
        sets.forEach((location, setBonusElement) -> {
            if (Arrays.stream(EquipmentSlot.values()).allMatch(slot ->
                    !setBonusElement.requiresSlot(slot) || setBonusElement.matchesItem(slot, equipment.get(slot)))
                    && !setBonusElement.isHidden()
            ) bonuses.put(location, setBonusElement);
        });
        return bonuses;
    }

    private class BonusLookup {
        private final LivingEntity target;
        private final Map<AbstractBonusElement, Reference<Integer>> activeBonuses = new HashMap<>();
        private final Map<SetBonusElement, SetData> setData = new HashMap<>();

        private BonusLookup(LivingEntity target) {
            getActiveBonuses(target).values().forEach(element -> {
                activeBonuses.put(element, Reference.of(0));
            });
            this.target = target;
        }

        public void tick() {
            this.activeBonuses.forEach((element, integer) -> {
                Bonus<?> bonus = element.getBonus();
                if (bonus.isEffectTick(integer.getIntValue(), target))
                    bonus.onTick(integer.getIntValue(), target);
                integer.setValue(integer.getIntValue() + 1);
            });
        }

        public void equipmentChange(EquipmentSlot slot, @NotNull ItemStack from, @NotNull ItemStack to) {
            List<AbstractBonusElement> previous = ImmutableList.copyOf(getBonusesForItem(from, true).values());
            List<AbstractBonusElement> next = ImmutableList.copyOf(getBonusesForItem(to, true).values());
            for (AbstractBonusElement element : previous) {
                if (!next.contains(element)) {
                    if (element instanceof SetBonusElement setBonusElement) {
                        SetData data = setData.get(element);
                        //skip if element isn't actually the equipped item
                        if (!setBonusElement.requiresSlot(slot) || !setBonusElement.matchesItem(slot, from)) continue;

                        data.removeEquipment(slot);
                    }
                    activeBonuses.remove(element);
                    Bonus<?> bonus = element.getBonus();
                    bonus.onRemove(target);
                    Multimap<Attribute, AttributeModifier> modifiers = bonus.getModifiers(this.target);
                    if (modifiers != null && !modifiers.isEmpty()) this.target.getAttributes().removeAttributeModifiers(modifiers);
                }
            }
            for (AbstractBonusElement element : next) {
                if (!previous.contains(element)) {
                    if (element instanceof SetBonusElement setElement) {
                        if (!setElement.requiresSlot(slot) || !setElement.matchesItem(slot, to)) continue;

                        SetData data = setData.computeIfAbsent(setElement, (setBonusElement) -> new SetData());
                        data.addEquipment(slot);
                        if (!data.checkActive(setElement)) {
                            continue;
                        }
                    }
                    Bonus<?> bonus = element.getBonus();
                    bonus.onApply(target);
                    Multimap<Attribute, AttributeModifier> modifiers = bonus.getModifiers(target);
                    if (modifiers != null && !modifiers.isEmpty()) target.getAttributes().addTransientAttributeModifiers(modifiers);
                    activeBonuses.put(element, Reference.of(0));
                }
            }
        }

        public void wearableChange(WearableSlot slot, ItemStack from, ItemStack to) {
            List<AbstractBonusElement> previous = ImmutableList.copyOf(getBonusesForItem(from, true).values());
            List<AbstractBonusElement> next = ImmutableList.copyOf(getBonusesForItem(to, true).values());
            for (AbstractBonusElement element : previous) {
                if (!next.contains(element)) {
                    if (element instanceof SetBonusElement setBonusElement) {
                        SetData data = setData.get(element);
                        //skip if element isn't actually the equipped item
                        if (!setBonusElement.requiresSlot(slot) || !setBonusElement.matchesItem(slot, from)) continue;

                        data.removeWearable(slot);
                    }
                    activeBonuses.remove(element);
                    Bonus<?> bonus = element.getBonus();
                    bonus.onRemove(target);
                    Multimap<Attribute, AttributeModifier> modifiers = bonus.getModifiers(this.target);
                    if (modifiers != null && !modifiers.isEmpty()) this.target.getAttributes().removeAttributeModifiers(modifiers);
                }
            }
            for (AbstractBonusElement element : next) {
                if (!previous.contains(element)) {
                    if (element instanceof SetBonusElement setElement) {
                        if (!setElement.requiresSlot(slot) || !setElement.matchesItem(slot, to)) continue;

                        SetData data = setData.computeIfAbsent(setElement, (setBonusElement) -> new SetData());
                        data.addWearable(slot);
                        if (!data.checkActive(setElement)) {
                            continue;
                        }
                    }
                    Bonus<?> bonus = element.getBonus();
                    bonus.onApply(target);
                    Multimap<Attribute, AttributeModifier> modifiers = bonus.getModifiers(target);
                    if (modifiers != null && !modifiers.isEmpty()) target.getAttributes().addTransientAttributeModifiers(modifiers);
                    activeBonuses.put(element, Reference.of(0));
                }
            }
        }

        public List<AbstractBonusElement> allActive() {
            return ImmutableList.copyOf(activeBonuses.keySet());
        }

        private static class SetData {

            //TODO convert to binary
            private long mask = 0;

            public void removeEquipment(EquipmentSlot slot) {
                this.mask &= ~(1L << slot.getFilterFlag());
            }

            public void addEquipment(EquipmentSlot slot) {
                this.mask |= 1L << slot.getFilterFlag();
            }

            public boolean checkActive(SetBonusElement element) {
                return element.requiredMask == this.mask;
            }

            public void addWearable(WearableSlot slot) {
                this.mask |= 1L << (slot.getSlotIndex() + 6);
            }

            public void removeWearable(WearableSlot slot) {
                this.mask &= ~(1L << (slot.getSlotIndex() + 6));
            }
        }
    }

    public BonusManager() {
        super(JsonHelper.GSON, "bonuses");
        MinecraftForge.EVENT_BUS.register(this);
        Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers = new HashMap<>();
        var event = new RegisterBonusProvidersEvent(providers);
        MinecraftForge.EVENT_BUS.post(event);
        this.providers = ImmutableMap.copyOf(providers);
    }

    //region load
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        pProfiler.push("loading bonuses");
        pObject.forEach((location, element) -> {
            pProfiler.push("element '" + location + "'");
            if (location.getPath().startsWith("set/")) readSetElement(location.withPath(s -> s.substring(4)), element);
            else readItemElement(location, element);
            pProfiler.pop();
        });
        pProfiler.pop();
    }

    private void readItemElement(ResourceLocation location, JsonElement element) {
        try {
            JsonObject main = element.getAsJsonObject();

            DataResult<Bonus<?>> result = ExtraCodecs.BONUS.parse(JsonOps.INSTANCE, main);

            Bonus<?> bonus = result.getOrThrow(false, s -> {});

            ResourceLocation itemLocation = new ResourceLocation(GsonHelper.getAsString(main, "item"));
            Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
            if (item == null) throw new IllegalArgumentException("unknown Item: " + itemLocation);

            boolean hidden = main.has("hidden") && GsonHelper.getAsBoolean(main, "hidden");
            addItemIfAbsent(item);
            BonusElement bonusElement = new BonusElement(hidden, bonus, location);
            this.bonusData.put(location, bonusElement);
            this.itemBonuses.putIfAbsent(item, location, bonusElement);
        } catch (Exception e) {
            LOGGER.warn(Markers.BONUS_MANAGER, "error loading item bonus '{}': {}", location, e.getMessage());
        }
    }

    private void readSetElement(ResourceLocation location, JsonElement jsonElement) {
        try {
            JsonObject main = jsonElement.getAsJsonObject();

            DataResult<Bonus<?>> result = ExtraCodecs.BONUS.parse(JsonOps.INSTANCE, main);

            Bonus<?> bonus = result.getOrThrow(false, s -> {});

            //read Item Tags
            Map<EquipmentSlot, TagKey<Item>> itemsForEquipmentSlot = new EnumMap<>(EquipmentSlot.class);
            //would 32 bits (26 slots) be enough?
            long required = 0;
            {
                JsonArray array = GsonHelper.getAsJsonArray(main, "equipment_slots");
                for (JsonElement element : array) {
                    EquipmentSlot slot = EquipmentSlot.byName(element.getAsString());
                    required |= 1L << slot.getFilterFlag();
                    itemsForEquipmentSlot.put(slot, TagKey.create(Registries.ITEM, location.withPath(s -> "set/" + s + "/" + slot.getName())));
                }
            }

            //read Item Tags
            Map<WearableSlot, TagKey<Item>> itemsForWearableSlot = new HashMap<>();
            {
                JsonArray array = GsonHelper.getAsJsonArray(main, "wearable_slots");
                for (JsonElement element : array) {
                    ResourceLocation location1 = new ResourceLocation(element.getAsString());
                    WearableSlot slot = ExtraRegistries.WEARABLE_SLOTS.getValue(location1);
                    if (slot == null) throw new IllegalStateException("unknown wearable slot: " + location1);
                    required |= 1L << (slot.getSlotIndex() + 6);
                    itemsForWearableSlot.put(slot, TagKey.create(Registries.ITEM, location.withPath(s -> "set/" + s + "/wearable/" + location1.getNamespace() + "/" + location1.getPath())));
                }
            }


            boolean hidden = main.has("hidden") && GsonHelper.getAsBoolean(main, "hidden");
            this.sets.put(location, new SetBonusElement(hidden, bonus, location, itemsForEquipmentSlot, itemsForWearableSlot, required));
        } catch (Exception e) {
            LOGGER.warn(Markers.BONUS_MANAGER, "error loading set bonus '{}': {}", location, e.getMessage());
        }
    }

    private void addItemIfAbsent(Item item) {
        if (!itemBonuses.containsKey(item)) {
            itemBonuses.put(item, new HashMap<>());
        }
    }
    //endregion

    //region getter

    private Map<ResourceLocation, AbstractBonusElement> getBonusesForItem(ItemStack stack, boolean ignoreHidden) {
        Map<ResourceLocation, BonusElement> itemBonuses = MapStream
                .of(Objects.requireNonNullElse(this.itemBonuses.get(stack.getItem()), Map.of()))
                .filterValues(bonusElement -> !bonusElement.hidden || ignoreHidden, null)
                .toMap();
        Map<ResourceLocation, SetBonusElement> setBonuses = MapStream.of(this.sets).filter((location, setBonusElement) ->
            setBonusElement.itemsForEquipmentSlot.values().stream().anyMatch(stack::is) || setBonusElement.itemsForWearableSlot.values().stream().anyMatch(stack::is)
        ).toMap();
        Map<ResourceLocation, AbstractBonusElement> extended = getAllExtended(stack);
        ImmutableMap.Builder<ResourceLocation, AbstractBonusElement> allBonuses = new ImmutableMap.Builder<>();
        allBonuses.putAll(itemBonuses);
        allBonuses.putAll(setBonuses);
        allBonuses.putAll(extended);
        return allBonuses.build();
    }

    private Map<ResourceLocation, AbstractBonusElement> getAllExtended(ItemStack stack) {
        Map<ResourceLocation, AbstractBonusElement> extended = new HashMap<>();
        this.providers.forEach((location, provider) -> {
            AbstractBonusElement e = provider.apply(stack);
            if (e != null) extended.put(location, e);
        });
        return extended;
    }

    @ApiStatus.Internal
    private Map<ResourceLocation, BonusElement> getActiveBonuses(LivingEntity living) {
        Map<ResourceLocation, BonusElement> bonuses = new HashMap<>();
        InventoryHelper.equipment(living).values()
                .stream()
                .map(ItemStack::getItem)
                .map(this.itemBonuses::get)
                .filter(Objects::nonNull)
                .forEach(bonuses::putAll);
        bonuses.putAll(getActiveSetBonuses(living));
        return bonuses;
    }

    //endregion

    //region tooltip

    public static List<Component> getBonusDisplay(ItemStack stack, @Nullable LivingEntity living) {
        if (instance == null) return List.of();
        Map<ResourceLocation, AbstractBonusElement> available = instance.getBonusesForItem(stack, false);

        List<Component> components = new ArrayList<>();

        available.forEach((location, bonus) -> {
            if (!bonus.isHidden()) components.addAll(instance.decorateBonus(living, bonus));
        });
        return components;
    }

    private List<Component> decorateBonus(@Nullable LivingEntity living, AbstractBonusElement element) {
        List<Component> decoration = new ArrayList<>();
        boolean enabled = RequirementManager.instance.meetsRequirements(RequirementType.BONUS, element, living);
        String nameKey = element.getNameId();
        decoration.add(getBonusTitle(enabled, living, nameKey, element));
        decoration.addAll(TextHelper.getDescriptionOrEmpty(nameKey, null));
        if (!enabled) ClientHelper.addReqContent(decoration::add, RequirementType.BONUS, element, living);
        return decoration;
    }

    private Component getBonusTitle(boolean enabled, @Nullable LivingEntity living, String title, AbstractBonusElement element) {
        MutableComponent name = Component.translatable(title);
        MutableComponent start = element.getTitle().withStyle((enabled ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY), ChatFormatting.BOLD);
        MutableComponent join1 = start.append(": ").append(name);
        if (element instanceof SetBonusElement setBonusElement) {
            Vec2i count = getSetBonusCount(living, setBonusElement);
            TextColor color = !enabled || count.x == 0 ?
                    TextColor.fromLegacyFormat(ChatFormatting.RED) :
                    new Color(ChatFormatting.GREEN)
                            .mix(
                                    new Color(ChatFormatting.RED),
                                    count.x / (float) count.y
                            ).toTextColor();
            join1.append(" (")
                    .append(Component.literal(String.valueOf(count.x)).withStyle(style -> style.withColor(color)))
                    .append("/")
                    .append(Component.literal(String.valueOf(count.y)).withStyle(ChatFormatting.DARK_AQUA))
                    .append(")");
        }
        return join1;
    }

    private Vec2i getSetBonusCount(@Nullable LivingEntity living, SetBonusElement element) {
        if (living == null) return Vec2i.ZERO;
        Optional<BonusLookup> optional = getLookup(living);
        int elementCount = Long.bitCount(element.requiredMask);
        if (optional.isPresent()) {
            BonusLookup lookup = optional.get();
            BonusLookup.SetData data = lookup.setData.get(element);
            if (data != null) {
                return new Vec2i(Long.bitCount(data.mask & element.requiredMask), elementCount);
            }
        }
        return new Vec2i(0, elementCount);
    }

    //endregion

    private static class SetBonusElement extends BonusElement {
        private final Map<EquipmentSlot, TagKey<Item>> itemsForEquipmentSlot;
        private final Map<WearableSlot, TagKey<Item>> itemsForWearableSlot;
        private final long requiredMask;

        private SetBonusElement(boolean hidden, Bonus<?> bonus, ResourceLocation location, Map<EquipmentSlot, TagKey<Item>> itemsForEquipmentSlot, Map<WearableSlot, TagKey<Item>> itemsForWearableSlot, long requiredMask) {
            super(hidden, bonus, location);
            this.itemsForEquipmentSlot = itemsForEquipmentSlot;
            this.itemsForWearableSlot = itemsForWearableSlot;
            this.requiredMask = requiredMask;
        }

        public boolean requiresSlot(EquipmentSlot slot) {
            return (this.requiredMask & (1L << slot.getFilterFlag())) != 0;
        }

        public boolean requiresSlot(WearableSlot slot) {
            return (this.requiredMask & (1L << (slot.getSlotIndex() + 6))) != 0;
        }

        public boolean matchesItem(EquipmentSlot slot, ItemStack stack) {
            return stack.is(itemsForEquipmentSlot.get(slot));
        }
        
        public boolean matchesItem(WearableSlot slot, ItemStack stack) {
            return stack.is(itemsForWearableSlot.get(slot));
        }

        public static void toNw(FriendlyByteBuf buf, SetBonusElement setBonusElement) {
            BonusElement.toNw(buf, setBonusElement);
            buf.writeMap(setBonusElement.itemsForEquipmentSlot,
                    FriendlyByteBuf::writeEnum,
                    (buf1, itemTagKey) -> buf1.writeResourceLocation(itemTagKey.location())
            );
            buf.writeMap(setBonusElement.itemsForWearableSlot,
                    (buf1, slot) -> buf1.writeRegistryIdUnsafe(ExtraRegistries.WEARABLE_SLOTS, slot),
                    (buf1, itemTagKey) -> buf1.writeResourceLocation(itemTagKey.location())
            );
            buf.writeLong(setBonusElement.requiredMask);
        }

        public static SetBonusElement fromNw(FriendlyByteBuf buf) {
            return new SetBonusElement(buf.readBoolean(), Bonus.fromNw(buf),
                    buf.readResourceLocation(),
                    buf.readMap(
                            buf1 -> buf1.readEnum(EquipmentSlot.class),
                            buf1 -> TagKey.create(Registries.ITEM, buf1.readResourceLocation())
                    ),
                    buf.readMap(
                            buf1 -> buf1.readRegistryIdUnsafe(ExtraRegistries.WEARABLE_SLOTS),
                            buf1 -> TagKey.create(Registries.ITEM, buf1.readResourceLocation())
                    ),
                    buf.readLong()
            );
        }

        @Override
        public MutableComponent getTitle() {
            return Component.translatable("set.bonus.name");
        }

        @Override
        public String getNameId() {
            return "set." + super.getNameId();
        }
    }

    public static class BonusElement implements AbstractBonusElement {
        private final boolean hidden;
        private final Bonus<?> bonus;
        private final ResourceLocation id;

        private BonusElement(boolean hidden, Bonus<?> bonus, ResourceLocation id) {
            this.hidden = hidden;
            this.bonus = bonus;
            this.id = id;
        }

        public boolean isHidden() {
            return hidden;
        }


        public Bonus<?> getBonus() {
            return bonus;
        }

        public static void toNw(FriendlyByteBuf buf, BonusElement bonusElement) {
            buf.writeBoolean(bonusElement.hidden);
            bonusElement.bonus.toNetwork(buf);
            buf.writeResourceLocation(bonusElement.id);
        }

        public static BonusElement fromNw(FriendlyByteBuf buf) {
            return new BonusElement(buf.readBoolean(), Bonus.fromNw(buf), buf.readResourceLocation());
        }

        public ResourceLocation getId() {
            return id;
        }

        @Override
        public MutableComponent getTitle() {
            return Component.translatable("bonus.name");
        }

        @Override
        public String getNameId() {
            return "bonus." + id.getNamespace() + "." + id.getPath();
        }
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeCollection(this.sets.values(), SetBonusElement::toNw);
        buf.writeMap(this.itemBonuses,
                (buf1, item) -> buf1.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item),
                (buf1, map) -> buf1.writeCollection(map.values(), BonusElement::toNw)
        );
    }

    @ApiStatus.Internal
    public static BonusManager fromNw(FriendlyByteBuf buf) {
        BonusManager manager = new BonusManager();
        ArrayList<SetBonusElement> setBonusElements = buf.readCollection(ArrayList::new, SetBonusElement::fromNw);
        manager.sets.putAll(setBonusElements.stream().collect(Collectors.toMap(SetBonusElement::getId, Function.identity())));
        manager.itemBonuses.putAll(
                buf.readMap(
                        buf1 -> buf1.readRegistryIdUnsafe(ForgeRegistries.ITEMS),
                        buf1 -> buf1.readCollection(ArrayList::new, BonusElement::fromNw)
                                .stream()
                                .collect(Collectors.toMap(BonusElement::getId, Function.identity()))
                )
        );
        return manager;
    }
}