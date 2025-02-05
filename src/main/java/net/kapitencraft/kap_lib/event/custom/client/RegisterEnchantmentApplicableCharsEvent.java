package net.kapitencraft.kap_lib.event.custom.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterEnchantmentApplicableCharsEvent extends Event {
    private final List<Pair<Item, Character>> data;
    private final List<Item> keys = new ArrayList<>();

    public RegisterEnchantmentApplicableCharsEvent(List<Pair<Item, Character>> data) {
        this.data = data;
        this.keys.addAll(this.data.stream().map(Pair::getFirst).toList());
    }

    public void register(Item item, Character c) {
        if (keys.contains(item)) throw new IllegalArgumentException("duplicate item in enchantment applicable chars: " + ForgeRegistries.ITEMS.getKey(item));
        keys.add(item);
        data.add(Pair.of(item, c));
    }
}
