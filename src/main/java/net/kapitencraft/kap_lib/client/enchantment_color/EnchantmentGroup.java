package net.kapitencraft.kap_lib.client.enchantment_color;

import net.kapitencraft.kap_lib.enchantments.abstracts.IUltimateEnchantment;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.IExtensibleEnum;

public enum EnchantmentGroup implements IExtensibleEnum, StringRepresentable {
    NORMAL,
    CURSE,
    ULTIMATE;

    public static final EnumCodec<EnchantmentGroup> CODEC = StringRepresentable.fromEnum(EnchantmentGroup::values);

    public static EnchantmentGroup create(String name) {
        throw new IllegalAccessError("Enum not extended!");
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    public Component getName() {
        return Component.translatable("enchantment_group." + getSerializedName());
    }

    public boolean is(Enchantment enchantment) {
        return switch (this) {
            case NORMAL -> true;
            case CURSE -> enchantment.isCurse();
            case ULTIMATE -> enchantment instanceof IUltimateEnchantment;
        };
    }
}
