package net.kapitencraft.kap_lib.two_handed.mixin.classes;

import net.kapitencraft.kap_lib.two_handed.TwoHanded;
import net.kapitencraft.kap_lib.two_handed.mixin.duck.TwoHandedPropertyInjector;
import net.kapitencraft.kap_lib.two_handed.registry.THItemComponents;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.Properties.class)
public class ItemPropertiesMixin implements TwoHandedPropertyInjector {

    @Override
    public Item.Properties twoHanded() {
        return ((Item.Properties) (Object) this).component(THItemComponents.TWO_HANDED, new TwoHanded(true));
    }
}
