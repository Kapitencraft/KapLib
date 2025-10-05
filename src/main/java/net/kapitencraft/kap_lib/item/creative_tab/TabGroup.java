package net.kapitencraft.kap_lib.item.creative_tab;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * used to group items into {@link CreativeModeTab}s
 */
public class TabGroup {
    private static final List<TabGroup> groups = new ArrayList<>();

    protected final Pair<Either<Supplier<Item>, Supplier<Item>>, ResourceKey<CreativeModeTab>>[] types;
    protected final List<RegistryObject<? extends Item>> items = new ArrayList<>();

    @SafeVarargs
    public TabGroup(Pair<Either<Supplier<Item>, Supplier<Item>>, ResourceKey<CreativeModeTab>>... type) {
        this.types = type;
        groups.add(this);
    }

    public TabGroup add(RegistryObject<? extends Item> toAdd) {
        items.add(toAdd);
        return this;
    }

    public void register(ResourceKey<CreativeModeTab> type, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> content) {
        for (Pair<Either<Supplier<Item>, Supplier<Item>>, ResourceKey<CreativeModeTab>> pair : types) {
            if (pair.getSecond().equals(type)) {
                Either<Supplier<Item>, Supplier<Item>> position = pair.getFirst();
                if (position == null) {
                    for (RegistryObject<? extends Item> item : items) {
                        content.put(new ItemStack(item.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    }
                    break;
                }
                position.ifRight(itemSupplier -> {
                    //after
                    ItemStack stack = new ItemStack(itemSupplier.get());
                    for (RegistryObject<? extends Item> item : items) {
                        content.putAfter(stack, new ItemStack(item.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    }
                }).ifLeft(itemSupplier -> {
                    //before
                    ItemStack stack = new ItemStack(itemSupplier.get());
                    for (RegistryObject<? extends Item> item : items) {
                        content.putBefore(stack, new ItemStack(item.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    }
                });
            }
        }
    }

    public static void registerAll(ResourceKey<CreativeModeTab> type, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> content) {
        groups.forEach(group -> group.register(type, content));
    }

    public static TabGroup create(RegistryObject<CreativeModeTab> obj) {
        return TabGroup.builder().tab(obj).build();
    }

    public static TabGroup create(ResourceKey<CreativeModeTab> key) {
        return TabGroup.builder().tab(key).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Pair<Either<Supplier<Item>, Supplier<Item>>, ResourceKey<CreativeModeTab>>> keys = new ArrayList<>();

        private Builder() {}

        public Builder tab(ResourceKey<CreativeModeTab> key) {
            this.keys.add(Pair.of(null, key));
            return this;
        }

        public Builder tabAfter(Supplier<Item> after, ResourceKey<CreativeModeTab> key) {
            this.keys.add(Pair.of(Either.right(after), key));
            return this;
        }

        public Builder tabBefore(Supplier<Item> before, ResourceKey<CreativeModeTab> key) {
            this.keys.add(Pair.of(Either.left(before), key));
            return this;
        }

        public Builder tab(RegistryObject<CreativeModeTab> tab) {
            return this.tab(tab.getKey());
        }

        public Builder tabAfter(Supplier<Item> after, RegistryObject<CreativeModeTab> value) {
            return this.tabAfter(after, value.getKey());
        }

        public Builder tabBefore(Supplier<Item> before, RegistryObject<CreativeModeTab> value) {
            return this.tabBefore(before, value.getKey());
        }

        public TabGroup build() {
            return new TabGroup(keys.toArray(Pair[]::new));
        }
    }
}