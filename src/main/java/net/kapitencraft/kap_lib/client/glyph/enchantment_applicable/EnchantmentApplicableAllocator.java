package net.kapitencraft.kap_lib.client.glyph.enchantment_applicable;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class EnchantmentApplicableAllocator extends FontSet {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static EnchantmentApplicableAllocator instance;

    public static final ResourceLocation FONT = KapLibMod.res("enchantment_applicable");
    private static final GlyphInfo GLYPH_INFO = new GlyphInfo() {
        @Override
        public float getAdvance() {
            return 10;
        }

        @Override
        public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> pGlyphProvider) {
            //IGNORED
            return null;
        }
    };
    private int index = 1;
    private int maxIndex = 24;
    private final GlyphRenderTypes renderTypes = GlyphRenderTypes.createForColorTexture(InventoryMenu.BLOCK_ATLAS);
    private BakedGlyph[] glyphs;

    public EnchantmentApplicableAllocator(TextureManager manager) {
        super(manager, FONT);
        this.init();
        instance = this;
    }

    public static EnchantmentApplicableAllocator getInstance() {
        return instance;
    }

    @Override
    public @NotNull GlyphInfo getGlyphInfo(int pCharacter, boolean pFilterFishyGlyphs) {
        return GLYPH_INFO;
    }

    public char addEntry(ResourceLocation location) {
        if (index >= maxIndex) {
            this.reallocate();
        }
        int index = this.index++;
        Minecraft.getInstance().tell(() -> this.add(location, index));

        return (char) index;
    }

    private void reallocate() {
        KapLibMod.LOGGER.debug("re-allocating player head atlas");
        BakedGlyph[] oldGlyphs = this.glyphs;
        BakedGlyph[] newGlyphs = new BakedGlyph[oldGlyphs.length * 2];
        System.arraycopy(oldGlyphs, 0, newGlyphs, 0, oldGlyphs.length);
        this.glyphs = newGlyphs;
        this.maxIndex = newGlyphs.length - 1;
    }

    private synchronized void add(ResourceLocation resourceLocation, int index) {
        Function<ResourceLocation, TextureAtlasSprite> atlasGetter = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite sprite = atlasGetter.apply(resourceLocation);
        addGlyph(index, sprite);
    }

    @Override
    public @NotNull BakedGlyph getGlyph(int pCharacter) {
        return glyphs[pCharacter];
    }

    @Override
    public @NotNull BakedGlyph getRandomGlyph(GlyphInfo pGlyph) {
        return glyphs[Mth.nextInt(KapLibMod.RANDOM_SOURCE, 0, this.index)];
    }

    private void addGlyph(int index, TextureAtlasSprite sprite) {
        glyphs[index] = new BakedGlyph(renderTypes, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), 0, 8, 2.5f, 11);
    }

    public void init() {
        this.glyphs = new BakedGlyph[25];
        index = 1;
        maxIndex = 24;
    }

    public void reset() {
        this.init();
    }
}