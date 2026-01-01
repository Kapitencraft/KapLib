package net.kapitencraft.kap_lib.bonus.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.kapitencraft.kap_lib.bonus.Bonus;
import net.kapitencraft.kap_lib.bonus.BonusManager;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.kapitencraft.kap_lib.inventory_page.event.custom.WearableSlotChangeEvent;
import net.kapitencraft.kap_lib.inventory_page.registry.custom.InventoryPageRegistries;
import net.kapitencraft.kap_lib.inventory_page.wearable.WearableSlot;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class InventoryPageCompat {

    public static BonusManager.SetBonusElement parseWithSlots(boolean hidden, Bonus<?> bonus, ResourceLocation location, Map<EquipmentSlot, TagKey<Item>> itemsForEquipmentSlot, JsonArray array, long required) {
        Map<WearableSlot, TagKey<Item>> itemsForWearableSlot = new HashMap<>();
        for (JsonElement element : array) {
            ResourceLocation location1 = ResourceLocation.parse(element.getAsString());
            WearableSlot slot = InventoryPageRegistries.WEARABLE_SLOTS.get(location1);
            if (slot == null) throw new IllegalStateException("unknown wearable slot: " + location1);
            required |= 1L << (slot.getSlotIndex() + 6);
            itemsForWearableSlot.put(slot, TagKey.create(Registries.ITEM, location.withPath(s -> "set/" + s + "/wearable/" + location1.getNamespace() + "/" + location1.getPath())));
        }

        return new WearableSlotSetBonusElement(hidden, bonus, location, itemsForEquipmentSlot, itemsForWearableSlot, required);
    }

    public static class WearableSlotSetBonusElement extends BonusManager.SetBonusElement {
        public static final StreamCodec<RegistryFriendlyByteBuf, ? super WearableSlotSetBonusElement> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, BonusManager.BonusElement::isHidden,
                Bonus.STREAM_CODEC, BonusManager.BonusElement::getBonus,
                ResourceLocation.STREAM_CODEC, BonusManager.BonusElement::getId,
                ByteBufCodecs.map(HashMap::new, ExtraStreamCodecs.EQUIPMENT_SLOT, ExtraStreamCodecs.tagKey(Registries.ITEM)), e -> e.itemsForEquipmentSlot,
                ByteBufCodecs.map(HashMap::new, WearableSlot.STREAM_CODEC, ExtraStreamCodecs.tagKey(Registries.ITEM)), e -> e.itemsForWearableSlot,
                ByteBufCodecs.VAR_LONG, e -> e.requiredMask,
                WearableSlotSetBonusElement::new
        );

        private final Map<WearableSlot, TagKey<Item>> itemsForWearableSlot;

        private WearableSlotSetBonusElement(boolean hidden, Bonus<?> bonus, ResourceLocation location, Map<EquipmentSlot, TagKey<Item>> itemsForEquipmentSlot, Map<WearableSlot, TagKey<Item>> itemsForWearableSlot, long requiredMask) {
            super(hidden, bonus, location, itemsForEquipmentSlot, requiredMask);
            this.itemsForWearableSlot = itemsForWearableSlot;
        }

        public boolean requiresSlot(WearableSlot slot) {
            return (this.requiredMask & (1L << (slot.getSlotIndex() + 6))) != 0;
        }

        public boolean matchesItem(WearableSlot slot, ItemStack stack) {
            return stack.is(itemsForWearableSlot.get(slot));
        }

        @Override
        public String getNameId() {
            return "set." + super.getNameId();
        }
    }

    @ApiStatus.Internal
    public static void onWearableSlotChange(WearableSlotChangeEvent event) {
        LivingEntity entity = event.getEntity();
        BonusManager.BonusLookup bonusLookup = BonusManager.instance.getOrCreateLookup(entity);
        bonusLookup.wearableChange(event.getSlot(), event.getFrom(), event.getTo());
    }
}
