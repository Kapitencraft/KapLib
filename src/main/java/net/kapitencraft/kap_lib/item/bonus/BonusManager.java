package net.kapitencraft.kap_lib.item.bonus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.collection.DoubleMap;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.InventoryHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.kapitencraft.kap_lib.requirements.type.BonusRequirementType;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//TODO possibly convert to DataPack registry?
@Mod.EventBusSubscriber
public class BonusManager extends SimpleJsonResourceReloadListener {

    public static BonusManager instance;

    public static BonusManager updateInstance() {
        if (instance != null) MinecraftForge.EVENT_BUS.unregister(instance);
        return instance = new BonusManager();
    }

    private Optional<BonusLookup> getLookup(LivingEntity living) {
        if (lookupMap.containsKey(living)) return Optional.of(lookupMap.get(living));
        return Optional.empty();
    }

    private BonusLookup getOrCreateLookup(LivingEntity living) {
        lookupMap.computeIfAbsent(living, BonusLookup::new);
        return lookupMap.get(living);
    }

    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        BonusLookup bonusLookup = getOrCreateLookup(entity);
        bonusLookup.equipmentChange(event.getSlot(), event.getFrom(), event.getTo());
        bonusLookup.removeEquipment(Pair.of(event.getFrom(), event.getSlot()));
        bonusLookup.addEquipment(Pair.of(event.getTo(), event.getSlot()));
    }

    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide()) return; //ONLY SERVERSIDE
        getLookup(event.getEntity()).ifPresent(BonusLookup::tick); //only tick when necessary
    }

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

    @Deprecated
    private Map<ResourceLocation, SetBonusElement> getActiveSetBonuses(LivingEntity living, boolean ignoreHidden) {
        Map<EquipmentSlot, ItemStack> equipment = InventoryHelper.equipment(living);
        Map<ResourceLocation, SetBonusElement> bonuses = new HashMap<>();
        sets.forEach((location, setBonusElement) -> {
            if (Arrays.stream(EquipmentSlot.values()).allMatch(slot ->
                    !setBonusElement.requiresSlot(slot) || setBonusElement.matchesItem(slot, equipment.get(slot)))
                    && !setBonusElement.isHidden() || ignoreHidden
            ) bonuses.put(location, setBonusElement);
        });
        return bonuses;
    }

    private class BonusLookup {
        private final LivingEntity target;
        private final Map<BonusElement, Reference<Integer>> activeBonuses = new HashMap<>();
        private final Map<SetBonusElement, List<EquipmentSlot>> setData = new HashMap<>();

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

        @SafeVarargs
        public final void removeEquipment(Pair<ItemStack, EquipmentSlot>... removed) {
            for (Pair<ItemStack, EquipmentSlot> pair : removed) {
                Map<ResourceLocation, BonusElement> bonuses = getBonusesForItem(pair.getFirst(), false);
                bonuses.values().forEach(element -> {
                    if (element instanceof SetBonusElement setBonusElement) {
                        List<EquipmentSlot> data = setData.get(element);
                        //skip if element isn't actually the equipped item
                        if (!setBonusElement.requiresSlot(pair.getSecond()) || !setBonusElement.matchesItem(pair.getSecond(), pair.getFirst())) return;

                        activeBonuses.remove(element);
                        data.remove(pair.getSecond());
                        if (data.isEmpty()) setData.remove(element);
                    } else activeBonuses.remove(element);
                    Bonus<?> bonus = element.getBonus();
                    bonus.onRemove(target);
                    Multimap<Attribute, AttributeModifier> modifiers = bonus.getModifiers(this.target);
                    if (modifiers != null && !modifiers.isEmpty()) this.target.getAttributes().removeAttributeModifiers(modifiers);
                });
            }
        }

        @SafeVarargs
        public final void addEquipment(Pair<ItemStack, EquipmentSlot>... added) {
            for (Pair<ItemStack, EquipmentSlot> pair : added) {
                Map<ResourceLocation, BonusElement> bonuses = getBonusesForItem(pair.getFirst(), false);
                bonuses.values().forEach(element -> {
                    if (element instanceof SetBonusElement setElement) {
                        setData.putIfAbsent(setElement, new ArrayList<>());
                        List<EquipmentSlot> data = setData.get(element);
                        data.add(pair.getSecond());
                        if (!new HashSet<>(data).containsAll(setElement.itemsForSlot.keySet())) {
                            return;
                        }
                    }
                    Bonus<?> bonus = element.getBonus();
                    bonus.onApply(target);
                    Multimap<Attribute, AttributeModifier> modifiers = bonus.getModifiers(target);
                    if (modifiers != null && !modifiers.isEmpty()) target.getAttributes().addTransientAttributeModifiers(modifiers);
                    activeBonuses.put(element, Reference.of(0));
                });
            }
        }

        public void equipmentChange(EquipmentSlot slot, @NotNull ItemStack from, @NotNull ItemStack to) {
            List<BonusElement> previous = ImmutableList.copyOf(getBonusesForItem(from, true).values());
            List<BonusElement> next = ImmutableList.copyOf(getBonusesForItem(to, true).values());
            for (BonusElement element : previous) {
                if (!next.contains(element)) {
                    if (element instanceof SetBonusElement setBonusElement) {
                        List<EquipmentSlot> data = setData.get(element);
                        //skip if element isn't actually the equipped item
                        if (!setBonusElement.requiresSlot(slot) || !setBonusElement.matchesItem(slot, from)) continue;

                        activeBonuses.remove(element);
                        data.remove(slot);
                        if (data.isEmpty()) setData.remove(element);
                    } else activeBonuses.remove(element);
                    Bonus<?> bonus = element.getBonus();
                    bonus.onRemove(target);
                    Multimap<Attribute, AttributeModifier> modifiers = bonus.getModifiers(this.target);
                    if (modifiers != null && !modifiers.isEmpty()) this.target.getAttributes().removeAttributeModifiers(modifiers);
                }
            }
            for (BonusElement element : next) {
                if (!previous.contains(element)) {
                    if (element instanceof SetBonusElement setElement) {
                        if (!setElement.requiresSlot(slot) || !setElement.matchesItem(slot, to)) continue;

                        setData.putIfAbsent(setElement, new ArrayList<>());
                        List<EquipmentSlot> data = setData.get(element);
                        data.add(slot);
                        if (!new HashSet<>(data).containsAll(setElement.itemsForSlot.keySet())) {
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
    }

    public BonusManager() {
        super(JsonHelper.GSON, "bonuses");
        MinecraftForge.EVENT_BUS.register(this);
    }

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
            KapLibMod.LOGGER.warn(Markers.BONUS_MANAGER, "error loading item bonus '{}': {}", location, e.getMessage());
        }
    }

    private void readSetElement(ResourceLocation location, JsonElement jsonElement) {
        try {
            JsonObject main = jsonElement.getAsJsonObject();

            DataResult<Bonus<?>> result = ExtraCodecs.BONUS.parse(JsonOps.INSTANCE, main);

            Bonus<?> bonus = result.getOrThrow(false, s -> {});

            //read Item Tags
            Map<EquipmentSlot, TagKey<Item>> itemsForSlot = new EnumMap<>(EquipmentSlot.class);
            {
                JsonArray array = GsonHelper.getAsJsonArray(main, "slots");
                for (JsonElement element : array) {
                    EquipmentSlot slot = EquipmentSlot.byName(element.getAsString());
                    itemsForSlot.put(slot, TagKey.create(Registries.ITEM, location.withPath(s -> "set/" + s + "/" + slot.getName())));
                }
            }

            boolean hidden = main.has("hidden") && GsonHelper.getAsBoolean(main, "hidden");
            this.sets.put(location, new SetBonusElement(hidden, bonus, location, itemsForSlot));
        } catch (Exception e) {
            KapLibMod.LOGGER.warn(Markers.BONUS_MANAGER, "error loading set bonus '{}': {}", location, e.getMessage());
        }
    }

    private void addItemIfAbsent(Item item) {
        if (!itemBonuses.containsKey(item)) {
            itemBonuses.put(item, new HashMap<>());
        }
    }

    public static List<Component> getBonusDisplay(ItemStack stack, @Nullable LivingEntity living) {
        if (instance == null) return List.of();
        Map<ResourceLocation, BonusElement> available = instance.getBonusesForItem(stack, true);

        List<Component> components = new ArrayList<>();

        available.forEach((location, bonus) -> components.addAll(instance.decorateBonus(living, location, bonus)));
        return components;
    }

    private Map<ResourceLocation, BonusElement> getBonusesForItem(ItemStack stack, boolean ignoreHidden) {
        Map<ResourceLocation, BonusElement> itemBonuses = MapStream
                .of(Objects.requireNonNullElse(this.itemBonuses.get(stack.getItem()), Map.of()))
                .filterValues(bonusElement -> !bonusElement.hidden || ignoreHidden, null)
                .toMap();
        Map<ResourceLocation, SetBonusElement> setBonuses = MapStream.of(this.sets).filter((location, setBonusElement) ->
            setBonusElement.itemsForSlot.values().stream().anyMatch(stack::is)
        ).toMap();
        ImmutableMap.Builder<ResourceLocation, BonusElement> allBonuses = new ImmutableMap.Builder<>();
        allBonuses.putAll(itemBonuses);
        allBonuses.putAll(setBonuses);
        return allBonuses.build();
    }

    @Deprecated
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
        boolean enabled = RequirementManager.instance.meetsRequirements(RequirementType.BONUS, element, living);
        String nameKey = (element instanceof SetBonusElement ? "set." : "") + "bonus." + bonusLocation.getNamespace() + "." + bonusLocation.getPath();
        decoration.add(getBonusTitle(enabled, living, nameKey, element));
        decoration.addAll(TextHelper.getDescriptionOrEmpty(nameKey, null));
        if (!enabled) ClientHelper.addReqContent(decoration::add, RequirementType.BONUS, element, living);
        return decoration;
    }

    private Component getBonusTitle(boolean enabled, @Nullable LivingEntity living, String title, BonusElement element) {
        boolean set = element instanceof SetBonusElement;
        MutableComponent name = Component.translatable(title);
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

    private static class SetBonusElement extends BonusElement {
        private final Map<EquipmentSlot, TagKey<Item>> itemsForSlot;

        private SetBonusElement(boolean hidden, Bonus<?> bonus, ResourceLocation location, Map<EquipmentSlot, TagKey<Item>> itemsForSlot) {
            super(hidden, bonus, location);
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

        public static void toNw(FriendlyByteBuf buf, SetBonusElement setBonusElement) {
            BonusElement.toNw(buf, setBonusElement);
            buf.writeMap(setBonusElement.itemsForSlot,
                    FriendlyByteBuf::writeEnum,
                    (buf1, itemTagKey) -> buf1.writeResourceLocation(itemTagKey.location())
            );
        }

        public static SetBonusElement fromNw(FriendlyByteBuf buf) {
            return new SetBonusElement(buf.readBoolean(), Bonus.fromNw(buf),
                    buf.readResourceLocation(),
                    buf.readMap(
                            buf1 -> buf1.readEnum(EquipmentSlot.class),
                            buf1 -> TagKey.create(Registries.ITEM, buf1.readResourceLocation())
                    )
            );
        }
    }

    public static class BonusElement {
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