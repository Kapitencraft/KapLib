package net.kapitencraft.kap_lib.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kapitencraft.kap_lib.event.custom.client.RegisterTridentModelsEvent;
import net.kapitencraft.kap_lib.item.combat.trident.AbstractTridentItem;
import net.kapitencraft.kap_lib.tags.ExtraTags;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;

public class ModBEWLR extends BlockEntityWithoutLevelRenderer {
    private static ModBEWLR instance;

    @ApiStatus.Internal
    public static ModBEWLR setInstance() {
        return instance = new ModBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static ModBEWLR getInstance() {
        return instance;
    }

    private Map<Item, Model> tridentModels;

    private static Map<Item, Model> reloadTridentModels(EntityModelSet modelSet) {
        return Util.make(Maps.newHashMap(), itemModelHashMap ->
                ModLoader.get().postEvent(new RegisterTridentModelsEvent(itemModelHashMap, modelSet))
        );
    }

    public ModBEWLR(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        this.tridentModels = reloadTridentModels(this.entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Item item = pStack.getItem();
        if (item instanceof AbstractTridentItem abstractTrident) {
            Model model = Objects.requireNonNull(tridentModels.get(item), "item '" + item.getDescriptionId() + "' added to trident rendered tag but not registered with model");
            pPoseStack.pushPose();
            pPoseStack.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(pBuffer, model.renderType(abstractTrident.getTexture(pStack)), false, pStack.hasFoil());
            model.renderToBuffer(pPoseStack, consumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
            pPoseStack.popPose();
        }
    }
}
