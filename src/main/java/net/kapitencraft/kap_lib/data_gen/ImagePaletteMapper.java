package net.kapitencraft.kap_lib.data_gen;

import com.mojang.blaze3d.platform.NativeImage;
import net.kapitencraft.kap_lib.util.Color;

import java.util.*;
import java.util.function.Function;

/**
 * idea and code by Startraveler. abridged and adapted
 */
public class ImagePaletteMapper {

    /**
     * get color abgr
     */
    public static Color extractColor(int abgrPixel) {
        int a = (abgrPixel >> 24) & 0xFF;
        int b = (abgrPixel >> 16) & 0xFF;
        int g = (abgrPixel >> 8) & 0xFF;
        int r = abgrPixel & 0xFF;
        return new Color(r, g, b, a);
    }

    /**
     * convert to abgr
     */
    public static int toABGR(Color color) {
        return color.pack();
    }

    /**
     * get alpga from an abgr pixel
     */
    public static int getAlpha(int abgrPixel) {
        return (abgrPixel >> 24) & 0xFF;
    }

    /**
     * put all opaques in a list
     */
    public static List<Color> loadOpaquePixels(NativeImage image) {
        List<Color> pixels = new ArrayList<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getPixelRGBA(x, y);
                int alpha = getAlpha(pixel);
                if (alpha != 0) { // no transparents
                    pixels.add(extractColor(pixel));
                }
            }
        }
        return pixels;
    }

    /**
     * calculate color distance
     */
    public static double colorDistance(Color c1, Color c2) {
        float dr = c1.r - c2.r;
        float dg = c1.g - c2.g;
        float db = c1.b - c2.b;
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * get n-many colors taht are maximally far apart
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
     * perceptual brightnesss calc
     */
    public static double brightness(Color color) {
        return 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
    }

    /**
     * sort dark-to-bright
     */
    public static List<Color> sortByBrightness(List<Color> colors) {
        List<Color> sorted = new ArrayList<>(colors);
        sorted.sort(Comparator.comparingDouble(ImagePaletteMapper::brightness));
        return sorted;
    }

    /**
     * get palette from img
     */
    public static List<Color> getPalette(NativeImage image, int paletteSize) {
        List<Color> opaquePixels = loadOpaquePixels(image);
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
    public static Function<Integer, Integer> makeColorMapper(List<Color> srcColors, List<Color> dstColors) {
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

        return (abgrPixel) -> {
            int alpha = getAlpha(abgrPixel);
            Color color = extractColor(abgrPixel);
            Color newColor = mapping.get(color);
            if (newColor != null) {
                return toABGR(newColor) & 0x00FFFFFF | alpha << 24; //re-add alpha
            }
            return abgrPixel; // unchanged if not in mapping
        };
    }

    /**
     * apply a mapping func to a given nativeimage
     */
    public static NativeImage mapImage(NativeImage source, Function<Integer, Integer> pixelMapper) {
        NativeImage result = new NativeImage(source.getWidth(), source.getHeight(), false);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int originalPixel = source.getPixelRGBA(x, y);
                int mappedPixel = pixelMapper.apply(originalPixel);
                result.setPixelRGBA(x, y, mappedPixel);
            }
        }

        return result;
    }

    /**
     * MAIN FUCKASS METHOD
     */
    public static NativeImage remapTexture(NativeImage paletteSource, NativeImage targetTexture, int paletteSize) {
        List<Color> sourcePalette = getPalette(paletteSource, paletteSize);
        List<Color> targetPalette = getPalette(targetTexture, paletteSize);

        Function<Integer, Integer> mapper = makeColorMapper(targetPalette, sourcePalette);
        return mapImage(targetTexture, mapper);
    }

    /**
     * MAIN FUCKASS METHOD WITH A DEFAULT 256 PALETTE SIZE
     */
    public static NativeImage remapTexture(NativeImage paletteSource, NativeImage targetTexture) {
        return remapTexture(paletteSource, targetTexture, 256);
    }
}
