package net.kapitencraft.kap_lib.data_gen.abst;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.collection.DoubleMap;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.io.serialization.DataPackSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.item.combat.armor.ModArmorItem;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * data generator for item bonuses
 */
//TODO fix provider hard-stuck
public abstract class BonusProvider extends ItemTagsProvider {
    private final PackOutput output;
    private final String modId;

    private final Map<String, SetBuilder> setBuilders = new HashMap<>();
    private final DoubleMap<Item, String, ItemBuilder> itemBuilders = DoubleMap.create();

    /**
     * creates a new BonusProvider
     * @param output the data output
     * @param modId the mod id
     * @param pLookupProvider access to the registries
     * @param existingFileHelper existing file helper
     */
    public BonusProvider(PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, pLookupProvider, new CompletableFuture<>(), modId, existingFileHelper);
        this.output = output;
        this.modId = modId;
    }

    protected SetBuilder createSetBonus(String name) {
        this.setBuilders.putIfAbsent(name, new SetBuilder(name));
        return this.setBuilders.get(name);
    }

    protected ItemBuilder createItemBonus(Item item, String name) {
        ItemBuilder builder = new ItemBuilder();
        this.itemBuilders.putIfAbsent(item, name, builder);
        return builder;
    }

    protected ItemBuilder createItemBonus(Supplier<? extends Item> supplier, String name) {
        return this.createItemBonus(supplier.get(), name);
    }

    /**
     * register your bonuses here
     * <br>using any of the following methods:
     * @see #createSetBonus(String)
     * @see #createItemBonus(Item, String)
     * @see #createItemBonus(Supplier, String)
     */
    public abstract void register();

    @Override
    @ApiStatus.Internal
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        register();
        List<? extends CompletableFuture<?>> setExecutors = MapStream.of(this.setBuilders)
            .mapToSimple((key, builder) -> {
                Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modId).resolve("bonuses").resolve("set")
                        .resolve(key + ".json");
                return DataProvider.saveStable(pOutput, saveSet(builder), path);
            }).toList();
        List<CompletableFuture<?>> itemExecutors = new ArrayList<>();
        this.itemBuilders.forAllEach((item, location, itemBuilder) -> {
            Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modId).resolve("bonuses")
                    .resolve(location + ".json");
            itemExecutors.add(DataProvider.saveStable(pOutput, saveItem(item, itemBuilder), path));
        });

        return CompletableFuture.allOf(
                CompletableFuture.allOf(setExecutors.toArray(CompletableFuture[]::new)),
                CompletableFuture.allOf(itemExecutors.toArray(CompletableFuture[]::new)),
                super.run(pOutput)
        );
    }

    private <T extends Bonus<T>> JsonObject saveItem(Item item, ItemBuilder itemBuilder) {
        T bonus = (T) itemBuilder.getBonus();
        JsonObject main = new JsonObject();
        if (itemBuilder.isHidden()) {
            main.addProperty("hidden", true);
        }
        {
            DataPackSerializer<T> serializer = bonus.getSerializer();
            main.add("data", serializer.serialize(bonus));
        }

        if (item != null) main.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item), "unknown item with class: " + item.getClass().getCanonicalName()).toString());
        main.addProperty("type", Objects.requireNonNull(ExtraRegistries.BONUS_SERIALIZER.getKey(itemBuilder.bonus.getSerializer()), "unknown bonus with class: " + itemBuilder.bonus.getClass().getCanonicalName()).toString());
        return main;
    }

    private JsonObject saveSet(SetBuilder builder) {
        JsonObject main = saveItem(null, builder);
        {
            JsonArray array = new JsonArray();
            for (EquipmentSlot slot : builder.content.keySet()) {
                array.add(slot.getName());
            }
            main.add("slots", array);
        }
        return main;
    }

    @Override
    public @NotNull String getName() {
        return "Bonuses of '" + modId + "'";
    }

    protected static class SetBuilder extends ItemBuilder {
        private final Map<EquipmentSlot, SetSlotBuilder> content = new HashMap<>();
        private final String name;

        protected SetBuilder(String name) {
            this.name = name;
        }

        public SetBuilder slot(EquipmentSlot slot, Consumer<SetSlotBuilder> builder) {
            content.putIfAbsent(slot, Util.make(new SetSlotBuilder(name, slot), builder));
            return this;
        }

        public SetBuilder slot(EquipmentSlot slot, Item item) {
            return this.slot(slot, setSlotBuilder -> setSlotBuilder.add(item));
        }

        public SetBuilder slot(EquipmentSlot slot, Supplier<? extends Item> supplier) {
            return this.slot(slot, supplier.get());
        }

        public SetBuilder armor(Map<ArmorItem.Type, ? extends RegistryObject<? extends ModArmorItem>> armors) {
            for (Map.Entry<ArmorItem.Type, ? extends RegistryObject<? extends ModArmorItem>> piece : armors.entrySet()) {
                EquipmentSlot slot = piece.getKey().getSlot();
                this.slot(slot, piece.getValue());
            }
            return this;
        }

        @Override
        public SetBuilder setHidden() {
            return (SetBuilder) super.setHidden();
        }

        @Override
        public SetBuilder setBonus(Bonus<?> bonus) {
            return (SetBuilder) super.setBonus(bonus);
        }

        private Map<EquipmentSlot, SetSlotBuilder> getContent() {
            return content;
        }
    }

    /**
     * slot builder. extends TagAppender to allow for tags to be used as item selector
     */
    protected static class SetSlotBuilder {
        private final TagKey<Item> key;
        private final TagBuilder builder;

        /**
         *
         */
        protected SetSlotBuilder(String setName, EquipmentSlot slot) {
            this.key = TagKey.create(Registries.ITEM, KapLibMod.res("set/" + setName + slot.getName()));
            this.builder = new TagBuilder();
        }

        public SetSlotBuilder add(Item item) {
            this.builder.add(TagEntry.element(item.builtInRegistryHolder().key().location()));
            return this;
        }

        /**
         * adds all the given items as possible for the builder
         */
        public SetSlotBuilder addAll(Item... items) {
            for (Item item : items) {
                this.add(item);
            }
            return this;
        }

        /**
         * adds this tag to the builder
         */
        public @NotNull BonusProvider.SetSlotBuilder addTag(@NotNull TagKey<Item> tagKey) {
            this.builder.addTag(tagKey.location());
            return this;
        }
    }

    protected static class ItemBuilder {
        private Bonus<?> bonus;
        private boolean hidden;

        /**
         * whether the description should be shown or not
         */
        public ItemBuilder setHidden() {
            this.hidden = true;
            return this;
        }

        /**
         * set the Bonus of this builder
         */
        public ItemBuilder setBonus(Bonus<?> bonus) {
            this.bonus = bonus;
            return this;
        }

        public boolean isHidden() {
            return hidden;
        }

        protected Bonus<?> getBonus() {
            return Objects.requireNonNull(bonus, "found builder without bonus!");
        }
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider pProvider) {
        this.setBuilders.values().forEach(setBuilder ->
                setBuilder.content.values().forEach(setSlotBuilder -> builders.put(setSlotBuilder.key.location(), setSlotBuilder.builder))
        );
    }
}
