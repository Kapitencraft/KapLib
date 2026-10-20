package net.kapitencraft.kap_lib.shader.mixin.classes.client;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.shader.BlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderType.class)
public class RenderTypeMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
    private static ImmutableList<Object> addRenderTypes(Object e1, Object e2, Object e3, Object e4, Object e5, Operation<ImmutableList<Object>> original) {
        List<Object> objects = new ArrayList<>();
        objects.add(e1);
        objects.add(e2);
        objects.add(e3);
        objects.add(e4);
        objects.add(e5);
        BlockRenderTypes.register();
        objects.addAll(BlockRenderTypes.RENDER_TYPES);
        return ImmutableList.copyOf(objects);    }
}
