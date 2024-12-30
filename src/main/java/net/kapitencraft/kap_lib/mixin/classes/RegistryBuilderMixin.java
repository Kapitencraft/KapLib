package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.registry.ExtraRegistryCallbacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryBuilder.class)
public abstract class RegistryBuilderMixin {

    @Shadow(remap = false) private ResourceLocation registryName;

    @Shadow(remap = false) public abstract RegistryBuilder<Object> addCallback(Object inst);

    @Inject(method = "create", at = @At("HEAD"), remap = false)
    private void addCallbacks(CallbackInfoReturnable<IForgeRegistry<Object>> cir) {
        if (this.registryName.equals(new ResourceLocation("minecraft:enchantment"))) {
            this.addCallback(new ExtraRegistryCallbacks.EnchantmentCallback());
        }
    }
}
