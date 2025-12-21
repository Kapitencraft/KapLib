package net.kapitencraft.kap_lib.mixin.classes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.core.tags.ExtraTags;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class CarvedPumpkinMixin {

    @Shadow @Final private static Predicate<BlockState> PUMPKINS_PREDICATE;

    @WrapOperation(method = {"getOrCreateSnowGolemFull", "getOrCreateIronGolemFull"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/pattern/BlockInWorld;hasState(Ljava/util/function/Predicate;)Ljava/util/function/Predicate;"))
    private Predicate<BlockInWorld> moveGolemHeadToTag(Predicate<BlockState> state, Operation<Predicate<BlockInWorld>> original) {
        if (state == PUMPKINS_PREDICATE) state = s -> s.is(ExtraTags.Blocks.VANILLA_GOLEM_HEADS);
        return original.call(state);
    }
}
