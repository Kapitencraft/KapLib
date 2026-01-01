package net.kapitencraft.kap_lib.mixin.classes;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.kapitencraft.kap_lib.item.modifier_display.EquipmentDisplayExtension;
import net.kapitencraft.kap_lib.item.modifier_display.ModifierDisplayManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;

//thanks to LlamaLad7 for helping me figure out how these annotations work
@Mixin(AttributeUtil.class)
public class AttributeUtilMixin {
    @Mixin(targets = {"net/neoforged/neoforge/common/util/AttributeUtil$BaseModifier"})
    private interface BaseModifierAccessor {

        @Accessor
        AttributeModifier getBase();

        @Accessor
        List<AttributeModifier> getChildren();
    }

    @ModifyReceiver(method = "applyTextFor", at = @At(value = "FIELD", target = "Lnet/neoforged/neoforge/common/util/AttributeUtil$BaseModifier;base:Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;", opcode = Opcodes.GETFIELD))
    private static @Coerce Object modifyBaseMod(@Coerce BaseModifierAccessor instance, @Share("mod") LocalRef<BaseModifierAccessor> mod) {
        mod.set(instance);
        return instance;
    }

    @WrapOperation(method = "applyTextFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/Attribute;toBaseComponent(DDZLnet/minecraft/world/item/TooltipFlag;)Lnet/minecraft/network/chat/MutableComponent;"))
    private static MutableComponent addAttributeExtensions(Attribute instance, double value, double entityBase, boolean merged, TooltipFlag tooltipFlag, Operation<MutableComponent> original, @Local(argsOnly = true, name = "arg0") ItemStack stack, @Share("mod") LocalRef<BaseModifierAccessor> mod) {
        MutableComponent component = original.call(instance, value, entityBase, merged, tooltipFlag);
        ModifierDisplayManager.ExtensionData extensions = ModifierDisplayManager.getExtensions(stack);
        for (EquipmentDisplayExtension equipmentProvider : extensions.equipmentProviders()) {
            AttributeModifier active = null;
            ResourceLocation location = equipmentProvider.getModifiersLocation();
            if (mod.get().getBase().id().equals(location))
                active = mod.get().getBase();
            else {
                for (AttributeModifier child : mod.get().getChildren()) {
                    if (child.id().equals(location)) {
                        active = child;
                        break;
                    }
                }
            }
            if (active != null) {
                component //actually append modifier
                        .append(CommonComponents.SPACE)
                        .append(equipmentProvider.createComponent(active.amount()));
            }
        }
        return component;
    }
}
