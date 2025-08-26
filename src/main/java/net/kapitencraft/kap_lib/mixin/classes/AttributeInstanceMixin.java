package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeInstance;
import net.kapitencraft.kap_lib.mixin.duck.attribute.IKapLibAttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AttributeInstance.class)
public abstract class AttributeInstanceMixin implements IKapLibAttributeInstance {
    @Shadow public abstract void removeModifier(AttributeModifier pModifier);

    @Unique
    private final List<IKapLibAttributeModifier> tickBased = new ArrayList<>();

    @Inject(method = "addModifier", at = @At("HEAD"))
    private void addTickBased(AttributeModifier pModifier, CallbackInfo ci) {
        IKapLibAttributeModifier modifier = IKapLibAttributeModifier.of(pModifier);
        if (modifier.tickBased()) {
            tickBased.add(modifier);
        }
    }

    @Override
    public void tick() {
        List<IKapLibAttributeModifier> toRemove = tickBased.stream().filter(IKapLibAttributeModifier::tick).toList();
        tickBased.removeAll(toRemove);
        toRemove.forEach(reg -> this.removeModifier((AttributeModifier) reg));
    }
}
