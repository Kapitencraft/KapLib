package net.kapitencraft.kap_lib.event.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class ItemStackEvent extends Event {

    private final ItemStack stack;

    public ItemStackEvent(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static class Load extends ItemStackEvent {

        public Load(ItemStack stack) {
            super(stack);
        }
    }

    public static class Save extends ItemStackEvent {
        private final CompoundTag tag;

        public Save(ItemStack stack, CompoundTag tag) {
            super(stack);
            this.tag = tag;
        }

        public CompoundTag getTag() {
            return tag;
        }
    }
}
