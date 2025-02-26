package net.kapitencraft.kap_lib.mixin.classes;

import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinMixin {

    @Shadow @Final private static Predicate<BlockState> PUMPKINS_PREDICATE;

    @Redirect(method = {"getOrCreateSnowGolemFull", "getOrCreateIronGolemFull"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/pattern/BlockInWorld;hasState(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;"))
    private Predicate<BlockInWorld> moveGolemHeadToTag(Predicate<BlockState> pState) {
        if (pState == PUMPKINS_PREDICATE) pState = state -> state.is(ExtraTags.Blocks.VANILLA_GOLEM_HEADS);
        return BlockInWorld.hasState(pState);
    }
}
