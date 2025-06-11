package net.kapitencraft.kap_lib.mixin.classes.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kapitencraft.kap_lib.inventory.wrapper.RecipeBookButtonWrapper;
import net.kapitencraft.kap_lib.inventory.page.InventoryPage;
import net.kapitencraft.kap_lib.inventory.page_renderer.InventoryPageRenderers;
import net.kapitencraft.kap_lib.inventory.page_renderer.InventoryPageRenderer;
import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageIO;
import net.kapitencraft.kap_lib.mixin.duck.inventory.InventoryPageReader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {

    @Shadow protected abstract boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY);

    @Shadow private float xMouse;

    @Shadow private float yMouse;

    @Shadow protected abstract void init();

    @Unique
    private InventoryPageRenderer[] renderers;

    @Unique
    private InventoryPageRenderer renderer;

    public InventoryScreenMixin(InventoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;setInitialFocus(Lnet/minecraft/client/gui/components/events/GuiEventListener;)V", shift = At.Shift.AFTER))
    private void initPages(CallbackInfo ci) {
        InventoryPageReader reader = (InventoryPageReader) this.menu;
        InventoryPage[] pages = reader.getPages();
        renderers = new InventoryPageRenderer[pages.length];
        for (int i = 0; i < pages.length; i++) {
            InventoryPageRenderer renderer = renderers[i] = InventoryPageRenderers.getRenderer(pages[i]).construct(pages[i]);
            renderer.init(leftPos, topPos);
        }
        this.renderer = renderers[reader.getPageIndex()];
    }

    @Redirect(method = "init", at = @At(value = "NEW", target = "(IIIIIIILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/components/Button$OnPress;)Lnet/minecraft/client/gui/components/ImageButton;"))
    private ImageButton wrapRecipeBookButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, Button.OnPress pOnPress) {
        return new RecipeBookButtonWrapper(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pOnPress, (InventoryPageReader) this.menu);
    }

    @Inject(method = "mouseClicked", at = @At(value = "RETURN", ordinal = 1))
    private void addPageSwap(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        int relativeY = (int) pMouseY - this.topPos;
        if (relativeY >= -32 && relativeY <= 0) {
            int relativeX = (int) pMouseX - this.leftPos;
            int index = relativeX / 28;
            InventoryPageIO pageIo = (InventoryPageIO) this.menu;
            if (index >= 0 && index < pageIo.getPages().length) {
                pageIo.setPage(index);
                this.renderer = this.renderers[index];
            }
        }
    }

    //TODO fix latest crash
    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void addPages(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, CallbackInfo ci) {
        InventoryPageReader reader = (InventoryPageReader) this.menu;
        int selected = reader.getPageIndex();
        InventoryPage[] pages = reader.getPages();
        for (int i = 0; i < pages.length; i++) {
            renderPageButton(pGuiGraphics, pages[i], selected == i, i);
        }
        if (selected != 0) {
            pGuiGraphics.blit(renderer.pageBackgroundLocation(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
            renderer.render(pGuiGraphics, this.minecraft, pMouseX, pMouseY, this.xMouse, this.yMouse, this.leftPos, this.topPos);
            ci.cancel();
        }
    }

    @Inject(method = "renderLabels", at = @At("HEAD"), cancellable = true)
    private void cancelIfOtherPage(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, CallbackInfo ci) {
        if (((InventoryPageReader) this.menu).getPageIndex() != 0) ci.cancel();
    }

    @Unique
    private void renderPageButton(GuiGraphics pGuiGraphics, InventoryPage page, boolean selected, int index)    {
        int j = index == 0 ? 0 : 26;
        int k = 0;
        int l = this.leftPos + index * 28;
        int i1 = this.topPos - 28;
        if (selected) {
            k += 32;
        }

        RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        pGuiGraphics.pose().pushPose();
        if (selected) pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        pGuiGraphics.blit(page.tabLocation(), l, i1, j, k, 26, 32);
        l += 5;
        i1 += 9;
        if (!selected) pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        ItemStack itemstack = Objects.requireNonNull(page.symbol(), "InventoryPage has null symbol! " + page);
        pGuiGraphics.renderItem(itemstack, l, i1);
        pGuiGraphics.renderItemDecorations(this.font, itemstack, l, i1);
        pGuiGraphics.pose().popPose();
    }
}
