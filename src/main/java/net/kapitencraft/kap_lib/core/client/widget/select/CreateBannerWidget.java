package net.kapitencraft.kap_lib.core.client.widget.select;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.core.client.BannerPatternRenderer;
import net.kapitencraft.kap_lib.core.client.UsefulTextures;
import net.kapitencraft.kap_lib.core.client.widget.PositionedWidget;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.kapitencraft.kap_lib.core.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * a widget to create a custom banner
 */
public class CreateBannerWidget extends PositionedWidget {
    private static final List<DyeColor> lights = List.of(DyeColor.WHITE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.LIGHT_BLUE);
    private final List<PositionedWidget> widgets = new ArrayList<>();
    private final VisualPatternBuilder builder;
    private final SelectDyeColorWidget selectDyeColorWidget;

    public CreateBannerWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
        selectDyeColorWidget = new SelectDyeColorWidget(this.x + 1, this.y + 1, 2, height);
        widgets.add(selectDyeColorWidget);
        int xStart1 = this.x + 2 + selectDyeColorWidget.width;
        SelectBannerPatternWidget selectBannerPatternWidget = new SelectBannerPatternWidget(xStart1, this.y + 1, 4, height);
        widgets.add(selectBannerPatternWidget);
        builder = new VisualPatternBuilder(xStart1 + 57, this.y + 1, height, List.of());
        widgets.add(builder);
    }

    /**
     * @return the generated banner
     */
    public ItemStack getBanner() {
        return builder.createBanner();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.widgets.forEach(positionedWidget -> positionedWidget.render(graphics, pMouseX, pMouseY, pPartialTick));
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double deltaX, double deltaY) {
        return this.widgets.stream().anyMatch(positionedWidget -> positionedWidget.mouseScrolled(pMouseX, pMouseY, deltaX, deltaY));
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return this.widgets.stream().anyMatch(positionedWidget -> positionedWidget.mouseClicked(pMouseX, pMouseY, pButton));
    }

    /**
     * 1. element: select a color for the pattern or background
     */
    private static class SelectDyeColorWidget extends MultiElementSelectorWidget<DyeColor> {
        /**
         * @param width  the width of the widget in colors <i>not</i> pixels
         */
        public SelectDyeColorWidget(int x, int y, int width, int height) {
            super(x, y, width, 16, height, List.of(DyeColor.values()));
            this.active = DyeColor.WHITE;
        }

        @Override
        protected void createElement(Consumer<MultiElementSelectorWidget<DyeColor>.ElementButton> adder, int xStart, int yStart, int elementSize, DyeColor element) {
            adder.accept(new ElementButton(xStart, yStart, 16, element, Color.fromARGBPacked(element.getTextColor()).setAlpha(255).pack()));
        }
    }

    /**
     * 2. element: select a banner pattern on click and push it together with the color from the 1. element to the 3. element
     */
    private class SelectBannerPatternWidget extends MultiElementSelectorWidget<Holder<BannerPattern>> {

        /**
         * @param width the width of the widget in elements, <i>not</i> pixels
         */
        public SelectBannerPatternWidget(int x, int y, int width, int height) {
            super(x, y, width, 14, height, getElements());
            this.active = null;
        }

        private static List<Holder<BannerPattern>> getElements() {
            HolderLookup.RegistryLookup<BannerPattern> lookup = Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN);
            return lookup.listElements().collect(Collectors.toUnmodifiableList());
        }

        @Override
        protected void createElement(Consumer<MultiElementSelectorWidget<Holder<BannerPattern>>.ElementButton> adder, int xStart, int yStart, int elementSize, Holder<BannerPattern> element) {
            adder.accept(new Element(xStart, yStart, element));
        }

        private class Element extends ElementButton {
            private final ModelPart flag = BannerPatternRenderer.getFlag();

            protected Element(int x, int y, Holder<BannerPattern> own) {
                super(x, y, 14, own, -1);
            }

            @Override
            public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                boolean flag = hovered(pMouseX, pMouseY);
                int j2 = 166;
                if (this.own == SelectBannerPatternWidget.this.active) {
                    j2+=14;
                } else if (flag) {
                    j2+= 28;
                }

                int xOffset = this.getXOffset();
                int yOffset = this.getYOffset();
                graphics.blit(UsefulTextures.SLIDER, xOffset, yOffset, 0, j2, 14, 14);
                this.renderPattern(CreateBannerWidget.this.selectDyeColorWidget.active);
            }

            @Override
            public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
                if (this.hovered(pMouseX, pMouseY)) {
                    CreateBannerWidget widget = CreateBannerWidget.this;
                    if (this.own.is(BannerPatterns.BASE)) {
                        widget.builder.background = widget.selectDyeColorWidget.active;
                        return true;
                    }
                    return widget.builder.addPattern(this.own, widget.selectDyeColorWidget.active);
                }
                return false;
            }

            @Contract("null -> fail")
            private void renderPattern(DyeColor color) {
                CompoundTag compoundtag = new CompoundTag();
                BannerPatternLayers layers = new BannerPatternLayers.Builder()
                        .add(this.own, color)
                        .build();
                ItemStack itemstack = new ItemStack(lights.contains(color) ? Items.GRAY_BANNER : Items.WHITE_BANNER);
                BlockItem.setBlockEntityData(itemstack, BlockEntityType.BANNER, compoundtag);
                PoseStack posestack = new PoseStack();
                posestack.pushPose();
                posestack.translate(this.getXOffset() + 0.5F, this.getYOffset() + 16, 0.0F);
                posestack.scale(6.0F, -6.0F, 1.0F);
                posestack.translate(0.5F, 0.5F, 0.0F);
                posestack.translate(0.5F, 0.5F, 0.0F);
                float f = 0.6666667F;
                posestack.scale(f, -f, -f);
                MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
                BannerRenderer.renderPatterns(posestack, source, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, DyeColor.GRAY, layers);
                posestack.popPose();
                source.endBatch();
            }
        }
    }

    /**
     * 3. element: show all added patterns and allow for modification like (re-) moving elements
     */
    private static class VisualPatternBuilder extends MultiElementSelectorWidget<Pair<Holder<BannerPattern>, DyeColor>> {
        private final ModelPart flag = BannerPatternRenderer.getFlag();
        private DyeColor background = DyeColor.WHITE;
        private int ySpawnOffset = 0;

        public VisualPatternBuilder(int x, int y, int height, List<Pair<Holder<BannerPattern>, DyeColor>> elements) {
            super(x, y, 1, 14, height, elements);
        }


        public boolean addPattern(Holder<BannerPattern> pBannerPattern, DyeColor pColor) {
            return this.addPattern(Pair.of(pBannerPattern, pColor));
        }

        public boolean addPattern(Pair<Holder<BannerPattern>, DyeColor> pPattern) {
            if (this.buttons.size() == 17) return false;
            this.buttons.add(new PatternElement(this.x, this.y + ySpawnOffset, pPattern));
            this.ySpawnOffset += 14;
            this.allElementsSize += 14;
            return true;
        }

        public boolean movePattern(@Range(from = 0, to = Integer.MAX_VALUE) int moveIndex, boolean up) {
            if (buttons.isEmpty() || up && moveIndex == 0 || !up && moveIndex + 1 == buttons.size()) return false; // cancel moving so you don't break stuff
            PatternElement pattern = (PatternElement) this.buttons.remove(moveIndex);
            if (up) {
                moveIndex--;
                this.buttons.add(moveIndex, pattern);
            }
            else {
                moveIndex++;
                this.buttons.add(moveIndex, pattern);
            }
            this.reapplyYPos();
            return true;
        }

        public void removePattern(int removeLoc) {
            this.buttons.remove(removeLoc);
            this.ySpawnOffset -= 14;
        }

        private void moveAllUp(int startId, int endId) {
            for (int i = startId; i <= endId; i++) {
                moveUp(i);
            }
        }

        private void moveUp(int elementId) {
            this.buttons.get(elementId).move(-14);
        }

        private void reapplyYPos() {
            for (int i = 0; i < this.buttons.size(); i++) {
                this.buttons.get(i).y = this.y + i * 14;
            }
        }

        @Override
        protected void createElement(Consumer<MultiElementSelectorWidget<Pair<Holder<BannerPattern>, DyeColor>>.ElementButton> adder, int xStart, int yStart, int elementSize, Pair<Holder<BannerPattern>, DyeColor> element) {
            adder.accept(new PatternElement(x, y, element));
        }

        @Override
        public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(graphics, pMouseX, pMouseY, pPartialTick);

            BannerPatternLayers layers = bakePatterns();
            BannerPatternRenderer.renderBanner(graphics, this.x + 10, this.y + 1, layers, this.background, this.height - 2);
        }

        private BannerPatternLayers bakePatterns() {
            return this.buttons
                    .stream()
                    .map(elementButton -> elementButton.own).collect(COLLECTOR);
        }

        private static final Collector<Pair<Holder<BannerPattern>, DyeColor>, BannerPatternLayers.Builder, BannerPatternLayers> COLLECTOR = Collector.of(
                BannerPatternLayers.Builder::new,
                (builder1, holderDyeColorPair) -> builder1.add(holderDyeColorPair.getFirst(), holderDyeColorPair.getSecond()),
                (builder1, builder2) -> {
                    builder1.addAll(builder2.build());
                    return builder1;
                },
                BannerPatternLayers.Builder::build
        );

        @Contract("null, _, _, _ -> fail; _, _, _, null -> fail")
        private void renderPattern(Holder<BannerPattern> pPattern, int pX, int pY, DyeColor color) {
            BannerPatternLayers layers = new BannerPatternLayers.Builder().add(pPattern, color).build();
            PoseStack posestack = new PoseStack();
            posestack.pushPose();
            posestack.translate(pX + .5F, pY + 16, 0.0F);
            posestack.scale(6.0F, -6.0F, 1.0F);
            posestack.translate(.5F, .5F, 0.0F);
            posestack.translate(.5F, .5F, 0F);
            float f = 0.6666667F;
            posestack.scale(f, -f, -f);
            MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
            BannerRenderer.renderPatterns(posestack, source, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, DyeColor.GRAY, layers);
            posestack.popPose();
            source.endBatch();
        }

        public ItemStack createBanner() {
            ItemStack stack = new ItemStack(getBannerItem(this.background));
            stack.set(DataComponents.BANNER_PATTERNS, bakePatterns());
            return stack;
        }

        private static Item getBannerItem(DyeColor color) {
            return switch (color) {
                case LIME -> Items.LIME_BANNER;
                case YELLOW -> Items.YELLOW_BANNER;
                case WHITE -> Items.WHITE_BANNER;
                case RED -> Items.RED_BANNER;
                case BLACK -> Items.BLACK_BANNER;
                case BLUE -> Items.BLUE_BANNER;
                case CYAN -> Items.CYAN_BANNER;
                case GRAY -> Items.GRAY_BANNER;
                case PINK -> Items.PINK_BANNER;
                case BROWN -> Items.BROWN_BANNER;
                case GREEN -> Items.GREEN_BANNER;
                case ORANGE -> Items.ORANGE_BANNER;
                case PURPLE -> Items.PURPLE_BANNER;
                case MAGENTA -> Items.MAGENTA_BANNER;
                case LIGHT_BLUE -> Items.LIGHT_BLUE_BANNER;
                case LIGHT_GRAY -> Items.LIGHT_GRAY_BANNER;
            };
        }

        private class PatternElement extends ElementButton {

            protected PatternElement(int x, int y, Pair<Holder<BannerPattern>, DyeColor> own) {
                super(x, y, 14, own, -1);
            }

            @Override
            public void render(@NotNull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                RenderSystem.setShaderTexture(0, UsefulTextures.SLIDER); //slider is also the LoomScreen texture
                boolean flag = MathHelper.is2dBetween(pMouseX, pMouseY, x, y, x + 14, y + 14);
                int j2 = 166;
                if (flag) {
                    j2+= 28;
                }

                int xOffset = getXOffset();
                int yOffset = getYOffset();
                graphics.blit(UsefulTextures.SLIDER, xOffset, yOffset, 0, j2, 14, 14);
                VisualPatternBuilder.this.renderPattern(own.getFirst(), xOffset, yOffset, own.getSecond());
                if (flag) {
                    UsefulTextures.renderCross(graphics, xOffset, yOffset + 7, 7);
                    int index = buttons.indexOf(this);
                    if (index != 0) UsefulTextures.renderUpButton(graphics, xOffset + 7, yOffset, MathHelper.is2dBetween(pMouseX, pMouseY, x + 7, y, x + 14, y + 7), 7);
                    if (index != buttons.size() - 1) UsefulTextures.renderDownButton(graphics, xOffset + 7, yOffset + 7, MathHelper.is2dBetween(pMouseX, pMouseY, x + 7, y + 7, x + 14, y + 14), 7);
                }
            }

            @Override
            public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
                if (hovered(pMouseX, pMouseY)) {
                    double relativeX = pMouseX - x;
                    double relativeY = pMouseY - y;
                    List<ElementButton> list = VisualPatternBuilder.this.buttons;
                    int index = list.indexOf(this);
                    if (relativeX < 7) {
                        if (relativeY >= 7) { //remove current pattern
                            removePattern(index);
                            moveAllUp(index, list.size() - 1);
                        }
                    } else { //move the pattern up or down
                        boolean ignored = movePattern(index, relativeY < 7); //It'll be used as soon as I figure out what the UI_BUTTON_FAIL_CLICK SoundEvent is
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                    }
                    return true;
                }
                return false;
            }
        }
    }
}