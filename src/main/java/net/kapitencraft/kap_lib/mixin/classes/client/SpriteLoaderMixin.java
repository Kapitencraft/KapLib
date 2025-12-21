package net.kapitencraft.kap_lib.mixin.classes.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kapitencraft.kap_lib.core.client.widget.background.texture.BackgroundTileableSection;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    @WrapOperation(method = "loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;ILjava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/SpriteLoader;loadAndStitch(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/ResourceLocation;ILjava/util/concurrent/Executor;Ljava/util/Collection;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<SpriteLoader.Preparations> getSections(SpriteLoader instance, ResourceManager resourceManager, ResourceLocation location, int mipLevel, Executor executor, Collection<MetadataSectionSerializer<?>> sectionSerializers, Operation<CompletableFuture<SpriteLoader.Preparations>> original) {
        if (location.equals(ResourceLocation.withDefaultNamespace("blocks"))) {
            ArrayList<MetadataSectionSerializer<?>> list = new ArrayList<>(sectionSerializers);
            list.add(BackgroundTileableSection.SERIALIZER);
            sectionSerializers = list;
        }
        return original.call(instance, resourceManager, location, mipLevel, executor, sectionSerializers);
    }
}
