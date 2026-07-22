package net.kapitencraft.kap_lib.two_handed;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface THItemTags {
    TagKey<Item> TWO_HANDED = TagKey.create(Registries.ITEM, LibConstants.res("two_handed"));
}