package net.kapitencraft.kap_lib.core.util;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@ApiStatus.Internal
public class ExtraRarityProxies {
    public static final EnumProxy<Rarity> LEGENDARY_PROXY = new EnumProxy<>(Rarity.class, -1, "kap_lib:legendary", ChatFormatting.GOLD);
    public static final EnumProxy<Rarity> MYTHIC_PROXY = new EnumProxy<>(Rarity.class, -1, "kap_lib:mythic", ChatFormatting.DARK_PURPLE);
    public static final EnumProxy<Rarity> DIVINE_PROXY = new EnumProxy<>(Rarity.class, -1, "kap_lib:divine", ChatFormatting.AQUA);


}
