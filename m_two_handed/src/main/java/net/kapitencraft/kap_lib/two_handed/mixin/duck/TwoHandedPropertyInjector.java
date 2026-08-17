package net.kapitencraft.kap_lib.two_handed.mixin.duck;

import net.kapitencraft.kap_lib.two_handed.TwoHanded;
import net.kapitencraft.kap_lib.two_handed.registry.THItemComponents;
import net.minecraft.world.item.Item;

public interface TwoHandedPropertyInjector {

    default Item.Properties twoHanded() {
        return twoHanded(true);
    }

    default Item.Properties twoHanded(boolean showInTooltip) {
        Item.Properties p = (Item.Properties) this;
        return p.component(THItemComponents.TWO_HANDED, new TwoHanded(showInTooltip));
    }
}
