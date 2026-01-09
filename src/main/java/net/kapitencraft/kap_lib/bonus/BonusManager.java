package net.kapitencraft.kap_lib.bonus;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.bonus.compat.InventoryPageCompat;
import net.kapitencraft.kap_lib.bonus.requirement.BonusRequirementType;
import net.kapitencraft.kap_lib.core.util.Modules;
import net.kapitencraft.kap_lib.core.collection.DoubleMap;
import net.kapitencraft.kap_lib.core.collection.MapStream;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.core.helpers.InventoryHelper;
import net.kapitencraft.kap_lib.core.helpers.MiscHelper;
import net.kapitencraft.kap_lib.core.helpers.TextHelper;
import net.kapitencraft.kap_lib.bonus.event.custom.RegisterBonusProvidersEvent;
import net.kapitencraft.kap_lib.inventory_page.wearable.WearableSlot;
import net.kapitencraft.kap_lib.core.io.JsonHelper;
import net.kapitencraft.kap_lib.bonus.network.S2C.UpdateBonusDataPacket;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.TerminatorTriggers;
import net.kapitencraft.kap_lib.requirement.RequirementManager;
import net.kapitencraft.kap_lib.core.util.Color;
import net.kapitencraft.kap_lib.core.util.Reference;
import net.kapitencraft.kap_lib.core.util.Vec2i;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
 * manages bonus related stuff like ticking active bonuses or listening to equipment changes and stores the registered bonuses
 */
public class BonusManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("BonusManager");

    public static BonusManager instance;
    public static BonusManager updateInstance() {
        if (instance != null) NeoForge.EVENT_BUS.unregister(instance);
        return instance = new BonusManager();
    }

    @ApiStatus.Internal
    public static void swapFrom(LivingEntity living, EquipmentSlot slot, ItemStack newItem, ItemStack oldItem) {
        instance.getOrCreateLookup(living).equipmentChange(slot, oldItem, newItem);
    }

    @ApiStatus.Internal
    public static float attackEvent(LivingEntity attacked, LivingEntity attacker, MiscHelper.DamageType type, float damage) {
        float[] damageWrapper = new float[] {damage};
        instance.getLookup(attacked).ifPresent(
                bonusLookup -> bonusLookup.activeBonuses.keySet().forEach(
                        abstractBonusElement -> damageWrapper[0] = abstractBonusElement.getBonus()
                                .onTakeDamage(attacked, attacker, type, damageWrapper[0])
                )
        );
        if (attacker != null) instance.getLookup(attacker).ifPresent(
                bonusLookup -> bonusLookup.activeBonuses.keySet().forEach(
                        abstractBonusElement -> damageWrapper[0] = abstractBonusElement.getBonus()
                                .onEntityHurt(attacked, attacker, type, damageWrapper[0])
                )
        );
        return damageWrapper[0];
    }

    @ApiStatus.Internal
    public static void deathEvent(LivingEntity toDie, DamageSource source) {
        LivingEntity attacker = MiscHelper.getAttacker(source);
        MiscHelper.DamageType type = MiscHelper.getDamageType(source);
        if (attacker != null) instance.getLookup(attacker).ifPresent(
                bonusLookup -> bonusLookup.activeBonuses.keySet().forEach(
                        abstractBonusElement -> abstractBonusElement.getBonus()
                                .onEntityKilled(toDie, attacker, type)
                )
        );
    }

    @ApiStatus.Internal
    public static boolean isDisabled(Entity entity, ResourceLocation elementId) {
        return entity instanceof LivingEntity living ? instance.getLookup(living)
                .map(l -> l.activeBonuses.keySet().stream().noneMatch(e -> e.getId() == elementId)).orElse(true) : true;
    }

    @ApiStatus.Internal
    public static void createFromData(Data data) {
        if (instance == null)
            instance = new BonusManager();
        instance.itemBonuses.putAll(data.itemBonuses);
        instance.sets.putAll(data.sets);
    }

    @ApiStatus.Internal
    private Optional<BonusLookup> getLookup(LivingEntity living) {
        if (lookupMap.containsKey(living)) return Optional.of(lookupMap.get(living));
        return Optional.empty();
    }

    /**
     * should not be used.
     * <br> only exposed to allow InventoryPage compat
     */
    @ApiStatus.Internal
    public BonusLookup getOrCreateLookup(LivingEntity living) {
        return lookupMap.computeIfAbsent(living, BonusLookup::new);
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        BonusLookup bonusLookup = getOrCreateLookup(entity);
        bonusLookup.equipmentChange(event.getSlot(), event.getFrom(), event.getTo());
        if (entity instanceof ServerPlayer sP) PacketDistributor.sendToPlayer(sP, new UpdateBonusDataPacket(event.getFrom(), event.getTo(), event.getSlot(), entity.getId()));
    }

    @ApiStatus.Internal
    @SubscribeEvent
    public void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity l && !l.level().isClientSide()) //ONLY SERVERSIDE
            getLookup(l).ifPresent(BonusLookup::tick); //only tick when necessary
    }

    private final Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers;

    private final Map<ResourceLocation, SetBonusElement> sets = new HashMap<>();
    private final Map<ResourceLocation, BonusElement> bonusData = new HashMap<>();
    private final DoubleMap<Item, ResourceLocation, BonusElement> itemBonuses = DoubleMap.create();
    private final Map<LivingEntity, BonusLookup> lookupMap = new HashMap<>();

    public final StreamCodec<? super RegistryFriendlyByteBuf, AbstractBonusElement> streamCodec = ResourceLocation.STREAM_CODEC.map(location -> {
        if (location.getPath().startsWith("set/")) {
            return getSetOrThrow(location.withPath(s -> s.substring(4)));
        }
        return getItemBonusOrThrow(location);
    }, AbstractBonusElement::getId);

    /**
     * gets a Setbonus for the given location or throws a Nullpointer exception if none could be found
     * @throws NullPointerException if there's no registered set bonus for the given location
     */
    public BonusElement getSetOrThrow(ResourceLocation location) {
        return Objects.requireNonNull(sets.get(location), "unknown set bonus: '" + location + "'");
    }

    /**
     * @param location the location of the Setbonus
     * @return the bonus element wrapped in an Optional or an empty optional, if there is no element
     */
    public Optional<BonusElement> getSet(ResourceLocation location) {
        return Optional.ofNullable(sets.get(location));
    }

    /**
     * gets an item bonus or throws a Nullpointer exception if none could be found
     * @param location the location of the bonus
     * @return the element
     * @throws NullPointerException if no registered item bonus could be found for the given location
     */
    public BonusElement getItemBonusOrThrow(ResourceLocation location) {
        return Objects.requireNonNull(bonusData.get(location), "unknown item bonus: '" + location + "'");
    }

    public Optional<BonusElement> getItemBonus(ResourceLocation location) {
        return Optional.ofNullable(bonusData.get(location));
    }

    /**
     * retrieves all (item, set or custom) bonuses that are active for the given entity
     * @param living the entity to retrieve all active bonuses for
     */
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

    @ApiStatus.Internal
    public Data createData() {
        return new Data(this.sets, this.itemBonuses);
    }

    @ApiStatus.Internal
    public Codec<AbstractBonusElement> getElementCodec() {
        return ResourceLocation.CODEC.comapFlatMap(location -> {
            if (location.getPath().startsWith("set/"))
                return this.getSet(location.withPath(s -> s.substring(4)))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "unknown set bonus: '" + location + "'"));
            return this.getItemBonus(location)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "unknown item bonus: '" + location + "'"));
        }, AbstractBonusElement::getId);
    }

    /**
     * do not use
     * <br> only exposed due to Compatibility with wearable slots
     */
    @ApiStatus.Internal
    public class BonusLookup {
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
                if (!next.contains(element) || (element instanceof SetBonusElement sBE && sBE.matchesItem(slot, from) && !sBE.matchesItem(slot, to))) {
                    if (element instanceof SetBonusElement setBonusElement) {
                        SetData data = setData.get(element);
                        //skip if element isn't actually the equipped item
                        if (!setBonusElement.requiresSlot(slot) || !setBonusElement.matchesItem(slot, from)) continue;

                        data.removeEquipment(slot);
                    }
                    activeBonuses.remove(element);
                    Bonus<?> bonus = element.getBonus();
                    bonus.onRemove(target);
                    if (this.target.level().isClientSide() && Modules.isParticleActive()) TerminatorTriggers.BONUS_REMOVED.get().trigger(this.target.getId(), element.getId());
                    Multimap<Holder<Attribute>, AttributeModifier> modifiers = bonus.getModifiers(this.target);
                    if (modifiers != null && !modifiers.isEmpty()) this.target.getAttributes().removeAttributeModifiers(modifiers);
                }
            }
            for (AbstractBonusElement element : next) {
                if (!previous.contains(element) || (element instanceof SetBonusElement sBE && sBE.matchesItem(slot, to) && !sBE.matchesItem(slot, from))) {
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
                    Multimap<Holder<Attribute>, AttributeModifier> modifiers = bonus.getModifiers(target);
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
                    if (element instanceof InventoryPageCompat.WearableSlotSetBonusElement setBonusElement) {
                        SetData data = setData.get(setBonusElement);
                        //skip if element isn't actually the equipped item
                        if (!setBonusElement.requiresSlot(slot) || !setBonusElement.matchesItem(slot, from))
                            continue;

                        data.removeWearable(slot);
                    }
                    activeBonuses.remove(element);
                    Bonus<?> bonus = element.getBonus();
                    bonus.onRemove(target);
                    if (this.target.level().isClientSide()) TerminatorTriggers.BONUS_REMOVED.get().trigger(this.target.getId(), element.getId());
                    Multimap<Holder<Attribute>, AttributeModifier> modifiers = bonus.getModifiers(this.target);
                    if (modifiers != null && !modifiers.isEmpty()) this.target.getAttributes().removeAttributeModifiers(modifiers);
                }
            }
            for (AbstractBonusElement element : next) {
                if (!previous.contains(element)) {
                    if (element instanceof InventoryPageCompat.WearableSlotSetBonusElement setElement) {
                        if (!setElement.requiresSlot(slot) || !setElement.matchesItem(slot, to))
                            continue;

                        SetData data = setData.computeIfAbsent(setElement, (setBonusElement) -> new SetData());
                        data.addWearable(slot);
                        if (!data.checkActive(setElement)) {
                            continue;
                        }
                    }
                    Bonus<?> bonus = element.getBonus();
                    bonus.onApply(target);
                    Multimap<Holder<Attribute>, AttributeModifier> modifiers = bonus.getModifiers(target);
                    if (modifiers != null && !modifiers.isEmpty()) target.getAttributes().addTransientAttributeModifiers(modifiers);
                    activeBonuses.put(element, Reference.of(0));
                }
            }
        }

        public List<AbstractBonusElement> allActive() {
            return ImmutableList.copyOf(activeBonuses.keySet());
        }

        private static class SetData {

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

            @Override
            public String toString() {
                return "SetData{mask = " + Long.toBinaryString(mask) + "}";
            }
        }
    }

    @ApiStatus.Internal
    private BonusManager() {
        super(JsonHelper.GSON, "bonuses");
        NeoForge.EVENT_BUS.register(this);
        if (Modules.isInventoryPageActive()) {
            NeoForge.EVENT_BUS.addListener(InventoryPageCompat::onWearableSlotChange);
        }
        Map<ResourceLocation, Function<ItemStack, AbstractBonusElement>> providers = new HashMap<>();
        var event = new RegisterBonusProvidersEvent(providers);
        NeoForge.EVENT_BUS.post(event);
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
        LOGGER.debug("loaded {} item and {} set bonuses", this.itemBonuses.size(), this.sets.size());
        pProfiler.pop();
    }

    private void readItemElement(ResourceLocation location, JsonElement element) {
        try {
            JsonObject main = element.getAsJsonObject();

            Bonus<?> bonus;

            if (main.has("bonus")) {
                DataResult<Bonus<?>> result = Bonus.CODEC.parse(JsonOps.INSTANCE, main.get("bonus"));

                bonus = result.resultOrPartial(e -> LOGGER.warn("unable to read item element bonus at {}: {}", location, e)).orElse(null);
            } else
                throw new NullPointerException("missing bonus");
            if (bonus == null) { //if bonus doesn't exist, skip
                return;
            }

            ResourceLocation itemLocation = ResourceLocation.parse(GsonHelper.getAsString(main, "item"));
            Item item = BuiltInRegistries.ITEM.get(itemLocation);

            boolean hidden = main.has("hidden") && GsonHelper.getAsBoolean(main, "hidden");
            addItemIfAbsent(item);
            BonusElement bonusElement = new BonusElement(hidden, bonus, location);
            this.bonusData.put(location, bonusElement);
            this.itemBonuses.putIfAbsent(item, location, bonusElement);
        } catch (Exception e) {
            LOGGER.warn("error loading item bonus '{}': {}", location, e.getMessage());
        }
    }

    private void readSetElement(ResourceLocation location, JsonElement jsonElement) {
        try {
            JsonObject main = jsonElement.getAsJsonObject();

            Bonus<?> bonus;
            if (main.has("bonus")) {
                DataResult<Bonus<?>> result = Bonus.CODEC.parse(JsonOps.INSTANCE, main.get("bonus"));

                bonus = result.resultOrPartial(e -> LOGGER.warn("unable to read set element bonus at {}: {}", location, e)).orElse(null);
            } else
                throw new NullPointerException("missing bonus");
            if (bonus == null) { //if bonus doesn't exist, skip
                return;
            }

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

            boolean hidden = main.has("hidden") && GsonHelper.getAsBoolean(main, "hidden");
            if (main.has("wearable_slots")) {
                if (Modules.isInventoryPageActive()) {
                    this.sets.put(location, InventoryPageCompat.parseWithSlots(hidden, bonus, location, itemsForEquipmentSlot, GsonHelper.getAsJsonArray(main, "wearable_slots"), required));
                    return;
                }
                LOGGER.warn("found wearable slots in element {} but the inventory page module is not installed!", location);
            }

            this.sets.put(location, new SetBonusElement(hidden, bonus, location, itemsForEquipmentSlot, required));
        } catch (Exception e) {
            LOGGER.warn("error loading set bonus '{}': {}", location, e.getMessage());
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
            setBonusElement.itemsForEquipmentSlot.values().stream().anyMatch(stack::is) //|| setBonusElement.itemsForWearableSlot.values().stream().anyMatch(stack::is)
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

    /**
     * gets the display of all non-hidden bonuses for the given stack and entity.
     * <br> if the entity is null, some extended info (like the amount of equipped pieces on a set bonus) will not show
     */
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
        boolean enabled = !Modules.isRequirementsActive() || RequirementManager.instance.meetsRequirements(BonusRequirementType.INSTANCE, element, living);
        String nameKey = element.getNameId();
        decoration.add(getBonusTitle(enabled, living, nameKey, element));
        decoration.addAll(TextHelper.getDescriptionOrEmpty(nameKey, null));
        if (!enabled && Modules.isRequirementsActive()) RequirementManager.addReqContent(decoration::add, BonusRequirementType.INSTANCE, element, living);
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
                    Color.fromFormatting(ChatFormatting.GREEN)
                            .mix(
                                    Color.fromFormatting(ChatFormatting.RED),
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

    /**
     * gets the amount of equipment pieces active and required for the given set bonus and entity
     * @param living the entity to check
     * @param element the element to check
     * @return a 2d integer vector containing 1. the amount of equipped and 2. the required amount of pieces
     */
    @ApiStatus.Internal
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

    //region data
    public static class SetBonusElement extends BonusElement {
        private static final StreamCodec<RegistryFriendlyByteBuf, SetBonusElement> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, BonusElement::isHidden,
                Bonus.STREAM_CODEC, BonusElement::getBonus,
                ResourceLocation.STREAM_CODEC, BonusElement::getId,
                ByteBufCodecs.map(HashMap::new, ExtraStreamCodecs.EQUIPMENT_SLOT, ExtraStreamCodecs.tagKey(Registries.ITEM)), e -> e.itemsForEquipmentSlot,
                ByteBufCodecs.VAR_LONG, e -> e.requiredMask,
                SetBonusElement::new
        );

        protected final Map<EquipmentSlot, TagKey<Item>> itemsForEquipmentSlot;
        protected final long requiredMask;

        protected SetBonusElement(boolean hidden, Bonus<?> bonus, ResourceLocation location, Map<EquipmentSlot, TagKey<Item>> itemsForEquipmentSlot, long requiredMask) {
            super(hidden, bonus, location);
            this.itemsForEquipmentSlot = itemsForEquipmentSlot;
            this.requiredMask = requiredMask;
        }

        public boolean requiresSlot(EquipmentSlot slot) {
            return (this.requiredMask & (1L << slot.getFilterFlag())) != 0;
        }

        public boolean matchesItem(EquipmentSlot slot, ItemStack stack) {
            return stack.is(itemsForEquipmentSlot.get(slot));
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
        public static final StreamCodec<RegistryFriendlyByteBuf, BonusElement> CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, BonusElement::isHidden,
                Bonus.STREAM_CODEC, BonusElement::getBonus,
                ResourceLocation.STREAM_CODEC, BonusElement::getId,
                BonusElement::new
        );

        private final boolean hidden;
        private final Bonus<?> bonus;
        private final ResourceLocation id;

        protected BonusElement(boolean hidden, Bonus<?> bonus, ResourceLocation id) {
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

    public record Data(Map<ResourceLocation, SetBonusElement> sets, DoubleMap<Item, ResourceLocation, BonusElement> itemBonuses) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, SetBonusElement.STREAM_CODEC), d -> d.sets,
                ExtraStreamCodecs.doubleMap(ByteBufCodecs.registry(Registries.ITEM), ResourceLocation.STREAM_CODEC, BonusElement.CODEC), Data::itemBonuses,
                Data::new
        );
    }
    //endregion
}