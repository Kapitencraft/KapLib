package net.kapitencraft.kap_lib.data_gen.abst;

import com.google.common.hash.HashCode;
import com.mojang.blaze3d.platform.NativeImage;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.util.Color;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntUnaryOperator;

/**
 * idea and code by Startraveler. abridged and adapted
 */
public abstract class TextureProvider implements DataProvider {
    private static final ExistingFileHelper.ResourceType TEXTURE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".png", "textures");

    private final ExistingFileHelper existingFileHelper;
    private final PackOutput output;

    private final List<Pipeline.Builder> converters = new ArrayList<>();

    public TextureProvider(ExistingFileHelper existingFileHelper, PackOutput output) {
        this.existingFileHelper = existingFileHelper;
        this.output = output;
    }

    //region color-transfer

    /**
     * get alpha from an RGBA pixel
     */
    public static int getAlpha(int rgbaPixel) {
        return (rgbaPixel >> 24) & 255;
    }

    /**
     * put all opaques in a list
     */
    public static List<Color> loadOpaquePixels(NativeImage image, @Nullable NativeImage mask) {
        List<Color> pixels = new ArrayList<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getPixelRGBA(x, y);
                int alpha = getAlpha(pixel);
                int maskAlpha = mask == null ? 255 : getAlpha(mask.getPixelRGBA(x, y));
                if (maskAlpha != 0 && alpha != 0) { // no transparents
                    pixels.add(Color.fromARGBPacked(pixel));
                }
            }
        }
        return pixels;
    }

    /**
     * calculate color distance
     */
    public static double colorDistance(Color c1, Color c2) {
        float dr = c1.r() - c2.r();
        float dg = c1.g() - c2.g();
        float db = c1.b() - c2.b();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * get n-many colors that are maximally far apart
     */
    public static List<Color> getMostUniqueColors(List<Color> pixels, int n) {
        if (pixels.isEmpty()) return new ArrayList<>();

        // count occurrences
        Map<Color, Integer> counter = new HashMap<>();
        for (Color pixel : pixels) {
            counter.merge(pixel, 1, Integer::sum);
        }

        List<Color> uniqueColors = new ArrayList<>(counter.keySet());
        if (uniqueColors.size() <= n) {
            return uniqueColors;
        }

        List<Color> selected = new ArrayList<>();
        // start with most frequent
        // get() call is guaranteed safe (?)
        Color mostFrequent = counter.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
        selected.add(mostFrequent);

        // greedy
        while (selected.size() < n) {
            Color bestColor = null;
            double bestDistance = -1;

            for (Color color : uniqueColors) {
                if (selected.contains(color)) continue;


                double minDist = selected.stream()
                        .mapToDouble(s -> colorDistance(color, s))
                        .min().orElse(0);

                if (minDist > bestDistance) {
                    bestDistance = minDist;
                    bestColor = color;
                }
            }

            if (bestColor != null) {
                selected.add(bestColor);
            } else {
                break; // no more distincts
            }
        }

        return selected;
    }

    /**
     * perceptual brightness calc
     */
    public static double brightness(Color color) {
        return 0.2126 * color.r() + 0.7152 * color.g() + 0.0722 * color.b();
    }

    /**
     * sort dark-to-bright
     */
    public static List<Color> sortByBrightness(List<Color> colors) {
        List<Color> sorted = new ArrayList<>(colors);
        sorted.sort(Comparator.comparingDouble(TextureProvider::brightness));
        return sorted;
    }

    /**
     * get palette from img
     */
    public static List<Color> getPalette(NativeImage image, NativeImage mask, int paletteSize) {
        List<Color> opaquePixels = loadOpaquePixels(image, mask);
        List<Color> uniqueColors = getMostUniqueColors(opaquePixels, paletteSize);
        return sortByBrightness(uniqueColors);
    }

    /**
     * lerp short palette to a longer palette's size
     */
    public static List<Color> expandToLength(List<Color> src, int targetLen) {
        if (src.isEmpty()) {
            throw new IllegalArgumentException("Cannot expand an empty palette");
        }
        if (targetLen <= src.size()) {
            return new ArrayList<>(src.subList(0, targetLen));
        }

        List<Color> result = new ArrayList<>();
        int nSrc = src.size();

        for (int i = 0; i < targetLen; i++) {
            float pos = i / (targetLen - 1f);
            Color color = getColor(src, pos, nSrc);
            result.add(color);
        }

        return result;
    }

    private static Color getColor(List<Color> src, float pos, int nSrc) {
        float srcPos = pos * (nSrc - 1);
        int idx = (int) srcPos;
        float frac = srcPos - idx;

        Color color;
        if (idx >= nSrc - 1) {
            color = src.get(nSrc - 1);
        } else {
            Color c1 = src.get(idx);
            Color c2 = src.get(idx + 1);
            // lerp
            color = c1.mix(c2, frac);
        }
        return color;
    }

    /**
     *make a mapper from one palette to another
     */
    public static IntUnaryOperator makeColorMapper(List<Color> srcColors, List<Color> dstColors) {
        List<Color> sortedSrc = sortByBrightness(new ArrayList<>(srcColors));
        List<Color> sortedDst = sortByBrightness(new ArrayList<>(dstColors));

        if (sortedSrc.size() > sortedDst.size()) {
            sortedDst = expandToLength(sortedDst, sortedSrc.size());
        } else if (sortedSrc.size() < sortedDst.size()) {
            sortedDst = sortByBrightness(getMostUniqueColors(sortedDst, sortedSrc.size()));
        }

        Map<Color, Color> mapping = new HashMap<>();
        for (int i = 0; i < sortedSrc.size(); i++) {
            mapping.put(sortedSrc.get(i), sortedDst.get(i));
        }

        return (rgbaPixel) -> {
            int alpha = getAlpha(rgbaPixel);
            Color color = Color.fromARGBPacked(rgbaPixel);
            Color newColor = mapping.get(color);
            if (newColor != null) {
                return newColor.pack() & 0x00FFFFFF | ((alpha & 255) << 24); //re-add alpha
            }
            return rgbaPixel; // unchanged if not in mapping
        };
    }

    /**
     * MAIN FUCKASS METHOD
     */
    public static NativeImage remapTexture(NativeImage paletteSource, NativeImage patternSource, NativeImage maskSource, int paletteSize) {
        List<Color> sourcePalette = getPalette(paletteSource, null, paletteSize);
        List<Color> patternPalette = getPalette(patternSource, maskSource, paletteSize);

        IntUnaryOperator mapper = makeColorMapper(patternPalette, sourcePalette);
        return patternSource.mappedCopy(mapper);
    }

    /**
     * MAIN FUCKASS METHOD WITH A DEFAULT 256 PALETTE SIZE
     */
    public static NativeImage remapTexture(NativeImage paletteSource, NativeImage targetTexture, NativeImage maskSource) {
        return remapTexture(paletteSource, targetTexture, maskSource, 256);
    }

    //endregion

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        this.createEntries();
        return CompletableFuture.allOf(this.converters.stream().map(Pipeline.Builder::build).map(p -> {
            if (p.passes.length == 0) throw new IllegalStateException("no passes in Pipeline " + p.in  + " -> " + p.output);
            if (!existingFileHelper.exists(p.in, TEXTURE)) throw new IllegalArgumentException("in target doesn't exist: " + p.in);
            for (Converter pass : p.passes) {
                pass.validate(existingFileHelper);
            }
            try {
                NativeImage image = NativeImage.read(existingFileHelper.getResource(p.in, PackType.CLIENT_RESOURCES, ".png", "textures").open());
                for (Converter pass : p.passes) {
                    NativeImage out = pass.convert(image, existingFileHelper);
                    image.close();
                    image = out;
                }
                existingFileHelper.trackGenerated(p.output, TEXTURE);
                Path path = this.output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "textures").file(p.output, "png");
                byte[] bytes = image.asByteArray();
                image.close();
                return CompletableFuture.runAsync(() -> {
                    try {
                        output.writeIfNeeded(path, bytes, HashCode.fromBytes(bytes));
                    } catch (IOException ex) {
                        throw new RuntimeException("Unable to store image: " + ex.getMessage());
                    }
                });

            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }).toArray(CompletableFuture[]::new));
    }

    /**
     * used to register entries via
     * {@link #register(ResourceLocation, ResourceLocation)}
     */
    protected abstract void createEntries();

    @Override
    public @NotNull String getName() {
        return "ImagePaletteMapper";
    }

    protected record Pipeline(ResourceLocation in, ResourceLocation output, Converter[] passes) {

        @SuppressWarnings("ClassEscapesDefinedScope")
        public static class Builder {
            private final ResourceLocation output, input;
            private final List<Converter> converters = new ArrayList<>();

            private Builder(ResourceLocation output, ResourceLocation input) {
                this.output = output;
                this.input = input;
            }

            public Builder then(Converter converter) {
                converters.add(converter);
                return this;
            }

            public Pipeline build() {
                return new Pipeline(input, output, converters.toArray(Converter[]::new));
            }
        }
    }

    protected interface Converter {

        //NativeImages save in ARGB format
        NativeImage convert(NativeImage in, ExistingFileHelper helper);

        default void validate(ExistingFileHelper helper) {}
    }

    //region shade
    protected record RedShade() implements Converter {
        public static RedShade create() {
            return new RedShade();
        }

        @Override
        public NativeImage convert(NativeImage in, ExistingFileHelper helper) {
            return in.mappedCopy(i -> i & 0xFFFF0000);
        }
    }

    protected record GreenShade() implements Converter {
        public static GreenShade create() {
            return new GreenShade();
        }

        @Override
        public NativeImage convert(NativeImage in, ExistingFileHelper helper) {
            return in.mappedCopy(i -> i & 0xFF00FF00);
        }
    }

    protected record BlueShade() implements Converter {
        public static BlueShade create() {
            return new BlueShade();
        }

        @Override
        public NativeImage convert(NativeImage in, ExistingFileHelper helper) {
            return in.mappedCopy(i -> i & 0xFF0000FF);
        }
    }
    //endregion

    protected record Transfer(ResourceLocation patternSource, ResourceLocation mask) implements Converter {
        /**
         * @param patternSource the texture to use the pattern of
         * @return a new transfer converter
         */
        public static Transfer create(ResourceLocation patternSource) {
            return new Transfer(patternSource, null);
        }

        public static Transfer createWithMask(ResourceLocation patternSource, ResourceLocation maskSource) {
            return new Transfer(
                    patternSource,
                    maskSource
            );
        }

        @Override
        public NativeImage convert(NativeImage in, ExistingFileHelper existingFileHelper) {
            try (NativeImage patternSource = NativeImage.read(existingFileHelper.getResource(this.patternSource, PackType.CLIENT_RESOURCES, ".png", "textures").open())) {
                if (mask != null) {
                    try (NativeImage mask = NativeImage.read(existingFileHelper.getResource(this.mask, PackType.CLIENT_RESOURCES, ".png", "textures").open())) {
                        return remapTexture(in, patternSource, mask);
                    }
                }
                return remapTexture(in, patternSource, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void validate(ExistingFileHelper helper) {
            if (!helper.exists(patternSource, TEXTURE)) {
                throw new IllegalStateException("unable to find pattern source: " + patternSource);
            }
        }
    }

    protected record Invert() implements Converter {

        public static Invert create() {
            return new Invert();
        }

        @Override
        public NativeImage convert(NativeImage in, ExistingFileHelper helper) {
            return in.mappedCopy(i -> {
                for (int j = 0; j < 3; j++) {
                    int val = (255 - (i >> (j * 8)) & 255);
                    int mask = 0xFF << (j * 8);
                    i = (i & ~mask) | (val << (j * 8));
                }
                return i;
            });
        }
    }

    protected record FlipX() implements Converter {

        @Override
        public NativeImage convert(NativeImage in, ExistingFileHelper helper) {
            int width = in.getWidth();
            int height = in.getHeight();
            NativeImage image = new NativeImage(width, height, false);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setPixelRGBA(x, y, image.getPixelRGBA(width - x - 1, y));
                }
            }
            return image;
        }
    }

    protected record FlipY() implements Converter {

        @Override
        public NativeImage convert(NativeImage in, ExistingFileHelper helper) {
            NativeImage image = in.mappedCopy(i -> i);
            image.flipY();
            return image;
        }
    }

    //region register
    /**
     * @param in the texture that is used as the base
     * @param out the output location
     * @return the Builder. use {@link Pipeline.Builder#then(Converter)} to add more passes
     */
    protected Pipeline.Builder register(ResourceLocation in, ResourceLocation out) {
        Pipeline.Builder builder = new Pipeline.Builder(out, in);
        this.converters.add(builder);
        return builder;
    }

    protected void registerOre(ResourceLocation paletteSource, ResourceLocation name) {
        this.register(paletteSource, name.withPrefix("item/raw_"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/raw_gold")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_dust"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/glowstone_dust")));
    }

    protected void registerTools(ResourceLocation paletteSource, ResourceLocation name) {
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_hoe"))
                .then(Transfer.createWithMask(ResourceLocation.withDefaultNamespace("item/golden_hoe"), KapLibMod.res("item/mask/hoe")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_sword"))
                .then(Transfer.createWithMask(ResourceLocation.withDefaultNamespace("item/golden_sword"), KapLibMod.res("item/mask/sword")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_pickaxe"))
                .then(Transfer.createWithMask(ResourceLocation.withDefaultNamespace("item/golden_pickaxe"), KapLibMod.res("item/mask/pickaxe")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_shovel"))
                .then(Transfer.createWithMask(ResourceLocation.withDefaultNamespace("item/golden_shovel"), KapLibMod.res("item/mask/shovel")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_axe"))
                .then(Transfer.createWithMask(ResourceLocation.withDefaultNamespace("item/golden_axe"), KapLibMod.res("item/mask/axe")));
    }

    protected void registerNetheriteArmor(ResourceLocation paletteSource, ResourceLocation name) {
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_helmet"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/netherite_helmet")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_chestplate"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/netherite_chestplate")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_leggings"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/netherite_leggings")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_boots"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/netherite_boots")));
        this.register(paletteSource, name.withPrefix("models/armor/").withSuffix("_layer_1"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("models/armor/netherite_layer_1")));
        this.register(paletteSource, name.withPrefix("models/armor/").withSuffix("_layer_2"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("models/armor/netherite_layer_2")));
    }

    protected void registerDiamondArmor(ResourceLocation paletteSource, ResourceLocation name) {
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_helmet"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/diamond_helmet")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_chestplate"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/diamond_chestplate")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_leggings"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/diamond_leggings")));
        this.register(paletteSource, name.withPrefix("item/").withSuffix("_boots"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("item/diamond_boots")));
        this.register(paletteSource, name.withPrefix("models/armor/").withSuffix("_layer_1"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("models/armor/diamond_layer_1")));
        this.register(paletteSource, name.withPrefix("models/armor/").withSuffix("_layer_2"))
                .then(Transfer.create(ResourceLocation.withDefaultNamespace("models/armor/diamond_layer_2")));
    }
    //endregion
}