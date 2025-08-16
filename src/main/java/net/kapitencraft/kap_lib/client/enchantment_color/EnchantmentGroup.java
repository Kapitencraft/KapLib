package net.kapitencraft.kap_lib.client.enchantment_color;

import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.jetbrains.annotations.NotNull;

public enum EnchantmentGroup implements IExtensibleEnum, StringRepresentable {
    NORMAL,
    CURSE,
    ULTIMATE;

    @SuppressWarnings("deprecation")
    public static final EnumCodec<EnchantmentGroup> CODEC = StringRepresentable.fromEnum(EnchantmentGroup::values);

    public static EnchantmentGroup create(String name) {
        throw new IllegalAccessError("Enum not extended!");
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase();
    }

    public Component getName() {
        return Component.translatable("enchantment_group." + getSerializedName());
    }

    public boolean is(Holder<Enchantment> enchantment) {
        return switch (this) {
            case NORMAL -> true;
            case CURSE -> enchantment.is(EnchantmentTags.CURSE);
            case ULTIMATE -> enchantment.is(ExtraTags.Enchantments.ULTIMATE);
        };
    }
}
