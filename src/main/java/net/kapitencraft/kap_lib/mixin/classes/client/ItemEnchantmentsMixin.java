package net.kapitencraft.kap_lib.mixin.classes.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.kapitencraft.kap_lib.client.enchantment_color.EnchantmentColorManager;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.enchantments.extras.EnchantmentDescriptionManager;
import net.kapitencraft.kap_lib.helpers.ClientHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(ItemEnchantments.class)
public abstract class ItemEnchantmentsMixin {

    @Shadow @Final private boolean showInTooltip;

    @Shadow
    private static <T> HolderSet<T> getTagOrEmpty(@Nullable HolderLookup.Provider registries, ResourceKey<Registry<T>> registryKey, TagKey<T> key) {
        return null;
    }

    @Shadow @Final private Object2IntOpenHashMap<Holder<Enchantment>> enchantments;

    /**
     * @author Kapitencraft
     * @reason add translations
     */
    @Overwrite
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (this.showInTooltip) {
            HolderLookup.Provider holderlookup$provider = context.registries();
            HolderSet<Enchantment> holderset = getTagOrEmpty(holderlookup$provider, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);

            for (Holder<Enchantment> holder : holderset) {
                int i = this.enchantments.getInt(holder);
                if (i > 0) {
                    EnchantmentDescriptionManager.addTooltip(tooltipAdder, holder, i);
                }
            }

            for (Object2IntMap.Entry<Holder<Enchantment>> entry : this.enchantments.object2IntEntrySet()) {
                Holder<Enchantment> holder1 = entry.getKey();
                if (!holderset.contains(holder1)) {
                    EnchantmentDescriptionManager.addTooltip(tooltipAdder, entry.getKey(), entry.getIntValue());
                }
            }
        }
    }
}
