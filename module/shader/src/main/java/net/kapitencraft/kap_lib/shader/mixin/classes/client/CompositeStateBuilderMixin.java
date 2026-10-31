package net.kapitencraft.kap_lib.shader.mixin.classes.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.shader.UniformRenderStateShard;
import net.kapitencraft.kap_lib.shader.patch.CompositeStateBuilderPatch;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;

@Mixin(RenderType.CompositeState.CompositeStateBuilder.class)
public class CompositeStateBuilderMixin implements CompositeStateBuilderPatch {
    private UniformRenderStateShard uniformRenderStateShard;

    @Override
    public RenderType.CompositeState.CompositeStateBuilder setUniformsStateShard(UniformRenderStateShard stateShard) {
        this.uniformRenderStateShard = stateShard;
        return (RenderType.CompositeState.CompositeStateBuilder) (Object) this;
    }

    @WrapOperation(method = "createCompositeState(Lnet/minecraft/client/renderer/RenderType$OutlineProperty;)Lnet/minecraft/client/renderer/RenderType$CompositeState;", at = @At(value = "NEW", target = "(Lnet/minecraft/client/renderer/RenderStateShard$EmptyTextureStateShard;Lnet/minecraft/client/renderer/RenderStateShard$ShaderStateShard;Lnet/minecraft/client/renderer/RenderStateShard$TransparencyStateShard;Lnet/minecraft/client/renderer/RenderStateShard$DepthTestStateShard;Lnet/minecraft/client/renderer/RenderStateShard$CullStateShard;Lnet/minecraft/client/renderer/RenderStateShard$LightmapStateShard;Lnet/minecraft/client/renderer/RenderStateShard$OverlayStateShard;Lnet/minecraft/client/renderer/RenderStateShard$LayeringStateShard;Lnet/minecraft/client/renderer/RenderStateShard$OutputStateShard;Lnet/minecraft/client/renderer/RenderStateShard$TexturingStateShard;Lnet/minecraft/client/renderer/RenderStateShard$WriteMaskStateShard;Lnet/minecraft/client/renderer/RenderStateShard$LineStateShard;Lnet/minecraft/client/renderer/RenderStateShard$ColorLogicStateShard;Lnet/minecraft/client/renderer/RenderType$OutlineProperty;)Lnet/minecraft/client/renderer/RenderType$CompositeState;"))
    private RenderType.CompositeState injectUniformProperty(
            RenderStateShard.EmptyTextureStateShard textureState,
            RenderStateShard.ShaderStateShard shaderState,
            RenderStateShard.TransparencyStateShard transparencyState,
            RenderStateShard.DepthTestStateShard depthState,
            RenderStateShard.CullStateShard cullState,
            RenderStateShard.LightmapStateShard lightmapState,
            RenderStateShard.OverlayStateShard overlayState,
            RenderStateShard.LayeringStateShard layeringState,
            RenderStateShard.OutputStateShard outputState,
            RenderStateShard.TexturingStateShard texturingState,
            RenderStateShard.WriteMaskStateShard writeMaskState,
            RenderStateShard.LineStateShard lineState,
            RenderStateShard.ColorLogicStateShard colorLogicState,
            RenderType.OutlineProperty outlineProperty,
            Operation<RenderType.CompositeState> original
    ) {

        RenderType.CompositeState state = original.call(textureState, shaderState, transparencyState, depthState, cullState, lightmapState,
                overlayState, layeringState, outputState, texturingState,
                writeMaskState,
                lineState,
                colorLogicState,
                outlineProperty
        );
        if (this.uniformRenderStateShard != null) {
            ArrayList<RenderStateShard> shards = new ArrayList<>(state.states);
            shards.add(this.uniformRenderStateShard);
            state.states = ImmutableList.copyOf(shards);
        }
        return state;
    }
}
