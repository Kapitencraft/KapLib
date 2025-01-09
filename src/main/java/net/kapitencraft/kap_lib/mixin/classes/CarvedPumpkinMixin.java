package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinMixin {

    @Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true, remap = false)
    private static void moveGolemHeadToTag(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.is(ExtraTags.Blocks.VANILLA_GOLEM_HEADS));
    }
}
