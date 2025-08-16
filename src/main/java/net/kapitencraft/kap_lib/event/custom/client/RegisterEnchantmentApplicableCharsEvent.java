package net.kapitencraft.kap_lib.event.custom.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * register items to be checked for enchantment display and the character to be displayed.
 * character must be registered to the {@code kap_lib:enchantment_applicable} font
 */
public class RegisterEnchantmentApplicableCharsEvent extends Event {
    private final BiConsumer<Item, ResourceLocation> sink;
    private final Consumer<Item> overloadSink;

    public RegisterEnchantmentApplicableCharsEvent(BiConsumer<Item, ResourceLocation> sink, Consumer<Item> overloadSink) {
        this.sink = sink;
        this.overloadSink = overloadSink;
    }

    /**
     * registers this item to the applicable chars list. <br>there must be a 16x16 texture available under
     * {@code <namespace>:textures/item/<item_id>}.
     * @param item the item to register. should be of diamond tier if possible
     */
    public void register(Item item) {
        overloadSink.accept(item);
    }

    /**
     * registers this item to the applicable chars list. <br>
     * the location must be inside the blocks texture atlas and of size 16x16
     * @param item the item to register. should be of diamond tier if possible
     * @param location the texture to use
     */
    public void register(Item item, ResourceLocation location) {
        sink.accept(item, location);
    }
}
