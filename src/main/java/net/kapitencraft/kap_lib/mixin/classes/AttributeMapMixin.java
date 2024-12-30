package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeInstance;
import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeMap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AttributeMap.class)
public class AttributeMapMixin implements IKapLibAttributeMap {

    @Shadow @Final private Map<Attribute, AttributeInstance> attributes;

    @Override
    public void tick() {
        this.attributes.values().stream().map(IKapLibAttributeInstance::of).forEach(IKapLibAttributeInstance::tick);
    }
}
