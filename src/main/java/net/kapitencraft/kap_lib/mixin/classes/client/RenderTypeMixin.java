package net.kapitencraft.kap_lib.mixin.classes.client;

import com.google.common.collect.ImmutableList;
import net.kapitencraft.kap_lib.client.shaders.BlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderType.class)
public class RenderTypeMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"), remap = false)
    private static ImmutableList<Object> addChunkLayers(Object e1, Object e2, Object e3, Object e4, Object e5) {
        List<Object> objects = new ArrayList<>();
        objects.add(e1);
        objects.add(e2);
        objects.add(e3);
        objects.add(e4);
        objects.add(e5);
        objects.addAll(BlockRenderTypes.RENDER_TYPES);
        return ImmutableList.copyOf(objects);
    }
}
