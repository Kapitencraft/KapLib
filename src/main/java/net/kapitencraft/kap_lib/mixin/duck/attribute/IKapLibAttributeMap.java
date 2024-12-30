package net.kapitencraft.kap_lib.mixin.duck.attribute;

import net.minecraft.world.entity.ai.attributes.AttributeMap;

public interface IKapLibAttributeMap {

    void tick();

    static IKapLibAttributeMap of(AttributeMap map) {
        return (IKapLibAttributeMap) map;
    }
}
