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

import java.util.Arrays;
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
    private int[] visible;

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
        visible = new int[pages.length];
        for (int i = 0; i < pages.length; i++) {
            InventoryPageRenderer renderer = renderers[i] = InventoryPageRenderers.getRenderer(pages[i]).construct(pages[i]);
            renderer.init(leftPos, topPos);
            visible[i] = i;
        }
        this.renderer = renderers[reader.getPageIndex()];
    }

    @Redirect(method = "init", at = @At(value = "NEW", target = "(IIIIIIILnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/gui/components/Button$OnPress;)Lnet/minecraft/client/gui/components/ImageButton;"))
    private ImageButton wrapRecipeBookButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, Button.OnPress pOnPress) {
        return new RecipeBookButtonWrapper(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pOnPress, (InventoryPageReader) this.menu);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleMouseClicks(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        int relativeY = (int) pMouseY - this.topPos;
        int relativeX = (int) pMouseX - this.leftPos;
        if (relativeX > 0 && relativeX < this.imageWidth) {
            if (relativeY > 0 && relativeY < this.imageHeight && ((InventoryPageReader) this.menu).getPageIndex() != 0) {
                cir.setReturnValue(this.renderer.onMouseClicked(relativeX, relativeY, pButton));
            } else if (relativeY >= -32 && relativeY <= 0) {
                InventoryPageIO pageIo = (InventoryPageIO) this.menu;
                int index = relativeX / 28;
                if (index < pageIo.getPages().length) {
                    if (visible[index] != -1) {
                        pageIo.setPage(visible[index]);
                        this.renderer = this.renderers[visible[index]];
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void handleMouseRelease(double pMouseX, double pMouseY, int pButton, CallbackInfoReturnable<Boolean> cir) {
        int relativeY = (int) pMouseY - this.topPos;
        int relativeX = (int) pMouseX - this.leftPos;
        if (relativeX > 0 && relativeX < this.imageWidth) {
            if (relativeY > 0 && relativeY < this.imageHeight && ((InventoryPageReader) this.menu).getPageIndex() != 0) {
                cir.setReturnValue(this.renderer.onMouseReleased(relativeX, relativeY, pButton));
            }
        }
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int relativeY = (int) pMouseY - this.topPos;
        int relativeX = (int) pMouseX - this.leftPos;
        if (relativeX > 0 && relativeX < this.imageWidth) {
            if (relativeY > 0 && relativeY < this.imageHeight && ((InventoryPageReader) this.menu).getPageIndex() != 0) {
                return this.renderer.onMouseDragged(relativeX, relativeY, pButton);
            }
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int relativeY = (int) pMouseY - this.topPos;
        int relativeX = (int) pMouseX - this.leftPos;
        if (relativeX > 0 && relativeX < this.imageWidth) {
            if (relativeY > 0 && relativeY < this.imageHeight && ((InventoryPageReader) this.menu).getPageIndex() != 0) {
                return this.renderer.onMouseScrolled(relativeX, relativeY, pDelta);
            }
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void addPages(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY, CallbackInfo ci) {
        InventoryPageReader reader = (InventoryPageReader) this.menu;
        int selected = reader.getPageIndex();
        InventoryPage[] pages = reader.getPages();
        int index = 0;
        Arrays.fill(visible, -1);
        for (int i = 0; i < pages.length; i++) {
            InventoryPage page = pages[i];
            if (page.isVisible(this.minecraft.player)) {
                visible[index] = i;
                renderPageButton(pGuiGraphics, page, selected == i, index++);
            };
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
    private void renderPageButton(GuiGraphics pGuiGraphics, InventoryPage page, boolean selected, int positionIndex)    {
        int j = positionIndex == 0 ? 0 : 26;
        int k = 0;
        int l = this.leftPos + positionIndex * 28;
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
