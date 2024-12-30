package net.kapitencraft.kap_lib.item.bonus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.collection.DoubleMap;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.InventoryHelper;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.kapitencraft.kap_lib.requirements.BonusRequirementType;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.kapitencraft.kap_lib.util.Color;
import net.kapitencraft.kap_lib.util.Reference;
import net.kapitencraft.kap_lib.util.Vec2i;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Mod.EventBusSubscriber
public class BonusManager extends SimpleJsonResourceReloadListener {

    public static BonusManager instance;

    public static BonusManager updateInstance(RegistryAccess registryAccess) {
        if (instance != null) MinecraftForge.EVENT_BUS.unregister(instance);
        instance = new BonusManager(registryAccess);
        return instance;
    }

    private BonusLookup getOrCreateLookup(LivingEntity living) {
        lookupMap.computeIfAbsent(living, BonusLookup::new); //TODO fix concurrent modification exception
        return lookupMap.get(living);
    }

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        BonusLookup bonusLookup = getOrCreateLookup(entity);
        bonusLookup.removeEquipment(Pair.of(event.getFrom(), event.getSlot()));
        bonusLookup.addEquipment(Pair.of(event.getTo(), event.getSlot()));
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        getOrCreateLookup(event.getEntity()).tick();
    }


    public static final Codec<List<TagEntry>> TAG_ENTRY_LOADER_CODEC = TagEntry.CODEC.listOf();
    private final RegistryAccess access;
    private final Map<ResourceLocation, SetBonusElement> sets = new HashMap<>();
    private final DoubleMap<Item, ResourceLocation, BonusElement> itemBonuses = DoubleMap.create();
    private final Map<LivingEntity, BonusLookup> lookupMap = new HashMap<>();

    private class BonusLookup {
        private final LivingEntity target;
        private final Map<BonusElement, Reference<Integer>> activeItemBonuses = new HashMap<>();
        private final Map<SetBonusElement, List<EquipmentSlot>> setData = new HashMap<>();

        private BonusLookup(LivingEntity target) {
            getActiveBonuses(target).values().forEach(element -> {
                activeItemBonuses.put(element, Reference.of(0));
            });
            this.target = target;
        }

        public void tick() {
            this.activeItemBonuses.forEach((element, integer) -> {
                element.getBonus().onTick(integer.getIntValue(), target);
                integer.setValue(integer.getIntValue() + 1);
            });
        }

        @SafeVarargs
        public final void removeEquipment(Pair<ItemStack, EquipmentSlot>... removed) {
            for (Pair<ItemStack, EquipmentSlot> pair : removed) {
                Map<ResourceLocation, BonusElement> bonuses = getBonusesForItem(pair.getFirst(), false);
                bonuses.values().forEach(element -> {
                    element.getBonus().onRemove(target);
                    if (element instanceof SetBonusElement) {
                        List<EquipmentSlot> data = setData.get(element);
                        activeItemBonuses.remove(element);
                        data.remove(pair.getSecond());
                        if (data.isEmpty()) setData.remove(element);
                    } else activeItemBonuses.remove(element);
                });
            }
        }

        @SafeVarargs
        public final void addEquipment(Pair<ItemStack, EquipmentSlot>... added) {
            for (Pair<ItemStack, EquipmentSlot> pair : added) {
                Map<ResourceLocation, BonusElement> bonuses = getBonusesForItem(pair.getFirst(), false);
                bonuses.values().forEach(element -> {
                    element.getBonus().onApply(target);
                    if (element instanceof SetBonusElement setElement) {
                        setData.putIfAbsent(setElement, new ArrayList<>());
                        List<EquipmentSlot> data = setData.get(element);
                        data.add(pair.getSecond());
                        if (new HashSet<>(data).containsAll(setElement.itemsForSlot.keySet())) {
                            activeItemBonuses.put(setElement, Reference.of(0));
                        }
                    } else activeItemBonuses.put(element, Reference.of(0));
                });
            }
        }
    }

    public BonusManager(RegistryAccess access) {
        super(JsonHelper.GSON, "bonuses");
        this.access = access;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        pProfiler.push("loading bonuses");
        pObject.forEach((location, element) -> {
            pProfiler.push("element '" + location + "'");
            if (location.getPath().startsWith("set/")) readSetElement(new ResourceLocation(location.getNamespace(), location.getPath().substring(4)), element);
            else readItemElement(location, element);
            pProfiler.pop();
        });
        pProfiler.pop();
    }

    private void readItemElement(ResourceLocation location, JsonElement element) {
        try {
            JsonObject main = element.getAsJsonObject();
            ResourceLocation itemLocation = new ResourceLocation(GsonHelper.getAsString(main, "item"));
            Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
            if (item == null) throw new IllegalArgumentException("unknown Item: " + itemLocation);
            DataGenSerializer<? extends Bonus<?>> serializer = readFromString(GsonHelper.getAsString(main, "type"));

            Bonus<?> bonus = serializer.deserialize(main.get("data"));
            boolean hidden = main.has("hidden") && GsonHelper.getAsBoolean(main, "hidden");
            addItemIfAbsent(item);
            this.itemBonuses.putIfAbsent(item, location, new BonusElement(hidden, bonus));
        } catch (Exception e) {
            KapLibMod.LOGGER.warn(Markers.BONUS_MANAGER, "error loading item bonus '{}': {}", location, e.getMessage());
        }
    }

    private void addItemIfAbsent(Item item) {
        if (!itemBonuses.containsKey(item)) {
            itemBonuses.put(item, new HashMap<>());
        }
    }

    private void addElementForItem(Item item, ResourceLocation location, BonusElement element) {
        addItemIfAbsent(item);
    }

    public static List<Component> getBonusDisplay(ItemStack stack, @Nullable LivingEntity living) {
        Map<ResourceLocation, BonusElement> available = instance.getBonusesForItem(stack, true);

        List<Component> components = new ArrayList<>();

        available.forEach((location, bonus) -> components.addAll(instance.decorateBonus(living, location, bonus)));
        return components;
    }

    private static DataGenSerializer<? extends Bonus<?>> readFromString(String string) {
        return ExtraRegistries.BONUS_SERIALIZER.getValue(new ResourceLocation(string));
    }

    private void readSetElement(ResourceLocation location, JsonElement jsonElement) {
        try {
            JsonObject main = jsonElement.getAsJsonObject();

            //read Item Tags
            Map<EquipmentSlot, TagKey<Item>> itemsForSlot = new HashMap<>();
            {
                JsonObject items = GsonHelper.getAsJsonObject(main, "items");
                Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagEntriesForBonus = new HashMap<>();
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (!items.has(slot.getName())) continue;
                    ResourceLocation locationForSlot = new ResourceLocation(location.getNamespace(), "set/" + location.getPath() + "/" + slot.getName());
                    JsonArray itemElements = GsonHelper.getAsJsonArray(items, slot.getName());
                    List<TagEntry> tagEntries = TAG_ENTRY_LOADER_CODEC.parse(JsonOps.INSTANCE, itemElements).getOrThrow(false, string -> KapLibMod.LOGGER.error(Markers.BONUS_MANAGER, "error loading bonus '{}': {}", location, string));
                    tagEntriesForBonus.put(locationForSlot,
                            tagEntries.stream()
                                    .map(tagEntry -> new TagLoader.EntryWithSource(tagEntry, locationForSlot.toString()))
                                    .toList()
                    );
                    itemsForSlot.put(slot, TagKey.create(Registries.ITEM, locationForSlot));
                }
                if (tagEntriesForBonus.size() < 2) throw new IllegalStateException("set bonuses require at least two used slots to work");
                TagLoader<Holder<Item>> loader = new TagLoader<>(ForgeRegistries.ITEMS::getHolder, "set_bonuses");
                Registry<Item> registry = this.access.registryOrThrow(Registries.ITEM);
                loader.build(tagEntriesForBonus).forEach((tagLocation, holders) -> {
                    TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagLocation);
                    registry.getOrCreateTag(tagKey).bind(List.copyOf(holders));
                });
            }
            //read Set Type and configuration
            DataGenSerializer<? extends Bonus<?>> serializer = readFromString(GsonHelper.getAsString(main, "type"));

            Bonus<?> bonus = serializer.deserialize(main.get("data"));
            boolean hidden = main.has("hidden") && GsonHelper.getAsBoolean(main, "hidden");
            this.sets.put(location, new SetBonusElement(hidden, bonus, itemsForSlot));
        } catch (Exception e) {
            KapLibMod.LOGGER.warn(Markers.BONUS_MANAGER, "error loading set bonus '{}': {}", location, e.getMessage());
        }
    }

    private Map<ResourceLocation, BonusElement> getBonusesForItem(ItemStack stack, boolean ignoreHidden) {
        Map<ResourceLocation, BonusElement> itemBonuses = MapStream
                .of(Objects.requireNonNullElse(this.itemBonuses.get(stack.getItem()), Map.of()))
                .filterValues(bonusElement -> !bonusElement.hidden || ignoreHidden, null)
                .toMap();
        Map<ResourceLocation, SetBonusElement> setBonuses = MapStream.of(this.sets).filter((location, setBonusElement) ->
            setBonusElement.itemsForSlot.values().stream().anyMatch(stack::is)
        ).toMap();
        Map<ResourceLocation, BonusElement> allBonuses = new HashMap<>();
        allBonuses.putAll(itemBonuses);
        allBonuses.putAll(setBonuses);
        return allBonuses;
    }

    private Map<ResourceLocation, BonusElement> getActiveBonuses(LivingEntity living) {
        Map<ResourceLocation, BonusElement> bonuses = new HashMap<>();
        InventoryHelper.equipment(living).values()
                .stream()
                .map(ItemStack::getItem)
                .map(this.itemBonuses::get)
                .filter(Objects::nonNull)
                .forEach(bonuses::putAll);
        bonuses.putAll(getActiveSetBonuses(living, false));
        return bonuses;
    }

    private List<Component> decorateBonus(@Nullable LivingEntity living, ResourceLocation bonusLocation, BonusElement element) {
        List<Component> decoration = new ArrayList<>();
        boolean enabled = RequirementManager.instance.meetsRequirements(BonusRequirementType.INSTANCE, element, living);
        decoration.add(getBonusTitle(enabled, living, bonusLocation, element));
        Bonus<?> bonus = element.bonus;
        bonus.addDisplay(decoration);
        ClientHelper.addReqContent(decoration::add, BonusRequirementType.INSTANCE, element, living);
        return decoration;
    }

    private Component getBonusTitle(boolean enabled, @Nullable LivingEntity living, ResourceLocation location, BonusElement element) {
        boolean set = element instanceof SetBonusElement setBonusElement;
        MutableComponent name = Component.translatable((set ? "set." : "") + "bonus." + location.getNamespace() + "." + location.getPath());
        MutableComponent start = Component.translatable((set ? "set." : "") + "bonus.name").withStyle((enabled ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY), ChatFormatting.BOLD);
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
        if (living == null) return new Vec2i(0, 0);
        List<Boolean> booleans = MapStream.of(InventoryHelper.equipment(living)).mapToSimple((slot, stack) -> element.itemsForSlot.containsKey(slot) ? stack.is(element.itemsForSlot.get(slot)) : null).filter(Objects::nonNull).toList();
        int c = 0;
        for (Boolean aBoolean : booleans) {
            if (aBoolean) c++;
        }
        return new Vec2i(c, booleans.size());
    }

    private Map<ResourceLocation, SetBonusElement> getActiveSetBonuses(LivingEntity living, boolean ignoreHidden) {
        Map<EquipmentSlot, ItemStack> equipment = InventoryHelper.equipment(living);
        Map<ResourceLocation, SetBonusElement> bonuses = new HashMap<>();
        sets.forEach((location, setBonusElement) -> {
            if (Arrays.stream(EquipmentSlot.values()).allMatch(slot ->
                    !setBonusElement.requiresSlot(slot) || setBonusElement.matchesItem(slot, equipment.get(slot)))
                    && !setBonusElement.isHidden() || ignoreHidden
            )
                bonuses.put(location, setBonusElement);
        });
        return bonuses;
    }

    private static class SetBonusElement extends BonusElement {
        private final Map<EquipmentSlot, TagKey<Item>> itemsForSlot;

        private SetBonusElement(boolean hidden, Bonus<?> bonus, Map<EquipmentSlot, TagKey<Item>> itemsForSlot) {
            super(hidden, bonus);
            this.itemsForSlot = itemsForSlot;
        }

        public boolean requiresSlot(EquipmentSlot slot) {
            return this.itemsForSlot.containsKey(slot);
        }

        public boolean matchesItem(EquipmentSlot slot, ItemStack stack) {
            return stack.is(itemsForSlot.get(slot));
        }

        /**
         * equality check for EquipmentsSlots, ItemStacks and other SetBonusElements
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof EquipmentSlot slot && this.requiresSlot(slot) ||
                    obj instanceof ItemStack stack && itemsForSlot.values().stream().anyMatch(stack::is) ||
                    obj instanceof SetBonusElement element &&
                        element.isHidden() == this.isHidden() &&
                        element.getBonus().equals(this.getBonus()) &&
                        element.itemsForSlot.equals(this.itemsForSlot);
        }
    }

    public static class BonusElement {
        private final boolean hidden;
        private final Bonus<?> bonus;

        private BonusElement(boolean hidden, Bonus<?> bonus) {
            this.hidden = hidden;
            this.bonus = bonus;
        }

        public boolean isHidden() {
            return hidden;
        }


        public Bonus<?> getBonus() {
            return bonus;
        }
    }
}