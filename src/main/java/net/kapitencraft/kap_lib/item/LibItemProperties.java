package net.kapitencraft.kap_lib.item;

import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class LibItemProperties {
    public static final ResourceLocation HOVERED_ID = KapLibMod.res("hovered");
    public static final String HOVERED_TAG_ID = "isHovered";

    static {
        ItemProperties.registerGeneric(HOVERED_ID, (pStack, pLevel, pEntity, pSeed) ->
                Optional.ofNullable(pStack.getTag())
                        .map(compoundTag -> compoundTag.getBoolean(HOVERED_TAG_ID))
                        .orElse(false)
                        ? 1 : 0
        );
    }
}
