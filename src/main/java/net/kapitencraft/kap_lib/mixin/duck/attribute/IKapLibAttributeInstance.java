package net.kapitencraft.kap_lib.mixin.duck.attribute;

import net.kapitencraft.kap_lib.mixin.classes.AttributeInstanceMixin;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.List;

public interface IKapLibAttributeInstance {

    void tick();

    static IKapLibAttributeInstance of(AttributeInstance instance) {
        return (IKapLibAttributeInstance) instance;
    }
}
