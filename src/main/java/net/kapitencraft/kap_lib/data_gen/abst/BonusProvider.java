package net.kapitencraft.kap_lib.data_gen.abst;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.collection.DoubleMap;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.io.serialization.DataGenSerializer;
import net.kapitencraft.kap_lib.item.bonus.Bonus;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;


import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class BonusProvider implements DataProvider {
    private final PackOutput output;
    private final String modId;

    private final Map<String, SetBuilder> setBuilders = new HashMap<>();
    private final DoubleMap<Item, String, ItemBuilder> itemBuilders = DoubleMap.create();

    public BonusProvider(PackOutput output, String modId) {
        this.output = output;
        this.modId = modId;
    }

    protected SetBuilder createSetBonus(String name) {
        this.setBuilders.putIfAbsent(name, new SetBuilder());
        return this.setBuilders.get(name);
    }

    protected ItemBuilder createItemBonus(Item item, String path) {
        ItemBuilder builder = new ItemBuilder();
        this.itemBuilders.putIfAbsent(item, path, builder);
        return builder;
    }

    public abstract void register();

    protected SlotBuilder simpleItem(Item item) {
        return new SlotBuilder().add(item);
    }

    protected SlotBuilder createBuilder() {
        return new SlotBuilder();
    }

    @Override
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
                CompletableFuture.allOf(itemExecutors.toArray(CompletableFuture[]::new))
        );
    }

    private <T extends Bonus<T>> JsonObject saveItem(Item item, ItemBuilder itemBuilder) {
        T bonus = (T) itemBuilder.getBonus();
        JsonObject main = new JsonObject();
        if (itemBuilder.isHidden()) {
            main.addProperty("hidden", true);
        }
        {
            DataGenSerializer<T> serializer = bonus.getSerializer();
            main.add("data", serializer.serialize(bonus));
        }

        if (item != null) main.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item), "unknown item with class: " + item.getClass().getCanonicalName()).toString());
        main.addProperty("type", Objects.requireNonNull(ExtraRegistries.BONUS_SERIALIZER.getKey(itemBuilder.bonus.getSerializer()), "unknown bonus with class: " + itemBuilder.bonus.getClass().getCanonicalName()).toString());
        return main;
    }

    private <T extends Bonus<T>> JsonObject saveSet(SetBuilder builder) {
        T bonus = (T) builder.getBonus();
        JsonObject main = saveItem(null, builder);
        {
            JsonObject items = new JsonObject();
            for (EquipmentSlot slot : builder.content.keySet()) {
                JsonElement element = BonusManager.TAG_ENTRY_LOADER_CODEC.encodeStart(JsonOps.INSTANCE, builder.content.get(slot).getInternalBuilder().build()).getOrThrow(false, KapLibMod.LOGGER::error);
                items.add(slot.getName(), element);
            }
            main.add("items", items);
        }
        return main;
    }

    @Override
    public @NotNull String getName() {
        return "Sets of '" + modId + "'";
    }

    protected class SetBuilder extends ItemBuilder {
        private final Map<EquipmentSlot, SlotBuilder> content = new HashMap<>();

        public SetBuilder slot(EquipmentSlot slot, SlotBuilder builder) {
            content.putIfAbsent(slot, builder);
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

        private Map<EquipmentSlot, SlotBuilder> getContent() {
            return content;
        }
    }

    protected class SlotBuilder extends TagsProvider.TagAppender<Item> {

        protected SlotBuilder() {
            super(new TagBuilder(), modId);
        }

        public SlotBuilder add(Item item) {
            super.add(item.builtInRegistryHolder().key());
            return this;
        }

        public SlotBuilder addAll(Item... items) {
            for (Item item : items) {
                this.add(item);
            }
            return this;
        }

        public @NotNull SlotBuilder addTag(@NotNull TagKey<Item> tagKey) {
            super.addTag(tagKey);
            return this;
        }
    }

    protected static class ItemBuilder {
        private Bonus<?> bonus;
        private boolean hidden;
        private String translationKey;

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
}
