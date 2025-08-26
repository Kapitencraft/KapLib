package net.kapitencraft.kap_lib.client.glyph.player_head;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.helpers.IOHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class PlayerHeadAllocator extends FontSet {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static PlayerHeadAllocator instance;

    public static final ResourceLocation FONT = KapLibMod.res("player_heads");
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
    private final SkinManager skinManager;
    private final TextureManager textureManager;
    private NativeImage atlas;
    private DynamicTexture atlasTexture;
    private final Map<UUID, Character> lookup;
    private final Map<UUID, FormattedText> textLookup;
    private int index = 0;
    private int maxIndex = 24;
    private final GlyphRenderTypes renderTypes = GlyphRenderTypes.createForColorTexture(FONT);
    private BakedGlyph[] glyphs;

    public PlayerHeadAllocator(SkinManager skinManager, TextureManager manager) {
        super(manager, FONT);
        this.skinManager = skinManager;
        this.textureManager = manager;
        this.lookup = new HashMap<>();
        this.textLookup = new HashMap<>();
        this.load();
        instance = this;
    }

    public static PlayerHeadAllocator getInstance() {
        return instance;
    }

    public FormattedText getTextForPlayer(UUID player) {
        return textLookup.computeIfAbsent(player, this::addPlayerText);
    }

    private FormattedText addPlayerText(UUID uuid) {
        return FormattedText.of(String.valueOf(PlayerHeadAllocator.getInstance().getGlyphForPlayer(uuid)), Style.EMPTY.withFont(PlayerHeadAllocator.FONT));
    }

    public char getGlyphForPlayer(UUID player) {
        return lookup.computeIfAbsent(player, this::addPlayer);
    }

    @Override
    public @NotNull GlyphInfo getGlyphInfo(int pCharacter, boolean pFilterFishyGlyphs) {
        return GLYPH_INFO;
    }

    private char addPlayer(UUID uuid) {
        GameProfile profile = new GameProfile(uuid, null);
        if (index >= maxIndex) {
            this.reallocate();
        }
        int index = this.index++;
        skinManager.registerSkins(profile, (type, resourceLocation, minecraftProfileTexture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                Minecraft.getInstance().tell(() -> this.addSkin(resourceLocation, index));
            }
        }, true);

        return (char) index;
    }

    private void reallocate() {
        KapLibMod.LOGGER.debug("re-allocating player head atlas");
        NativeImage original = this.atlas;
        NativeImage image = new NativeImage(original.getWidth() * 2, original.getHeight(), false);
        original.copyRect(image, 0, 0, 0, 0, original.getWidth(), original.getHeight(), false, false);
        original.close();
        this.atlas = image;
        this.atlasTexture = new DynamicTexture(atlas);
        this.textureManager.register(FONT, atlasTexture);
        BakedGlyph[] oldGlyphs = this.glyphs;
        BakedGlyph[] newGlyphs = new BakedGlyph[oldGlyphs.length * 2];
        System.arraycopy(oldGlyphs, 0, newGlyphs, 0, oldGlyphs.length);
        this.glyphs = newGlyphs;
        this.maxIndex = newGlyphs.length - 1;
    }

    private synchronized void addSkin(ResourceLocation resourceLocation, int index) {
        Minecraft.getInstance().execute(() -> {
            RenderTarget renderTarget = new TextureTarget(72, 72, true, false);
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

            //necessary
            Matrix4f matrix = new Matrix4f();
            matrix.setOrtho(0.0F, 72, 72, 0.0F, -1.0F, 1.0F);
            RenderSystem.setProjectionMatrix(matrix, VertexSorting.ORTHOGRAPHIC_Z);
            RenderSystem.applyModelViewMatrix();

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            renderTarget.bindWrite(true);

            RenderSystem.clearColor(0, 1, 1, 0);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true);

            float headStart = 8f / 64f;
            float headEnd = 16f / 64f;

            bufferBuilder.vertex(4, 4, 0).uv(headStart, headStart).endVertex(); //TL
            bufferBuilder.vertex(4, 68, 0).uv(headStart, headEnd).endVertex(); //BL
            bufferBuilder.vertex(68, 68, 0).uv(headEnd, headEnd).endVertex(); //BR
            bufferBuilder.vertex(68, 4, 0).uv(headEnd, headStart).endVertex(); //TR

            float hatUStart = 40f / 64f;
            float hatVStart = 8f / 64f;
            float hatUEnd = 48f / 64f;
            float hatVEnd = 16f / 64f;

            bufferBuilder.vertex(0, 0, 0).uv(hatUStart, hatVStart).endVertex(); //TL
            bufferBuilder.vertex(0, 72, 0).uv(hatUStart, hatVEnd).endVertex(); //BL
            bufferBuilder.vertex(72, 72, 0).uv(hatUEnd, hatVEnd).endVertex(); //BR
            bufferBuilder.vertex(72, 0, 0).uv(hatUEnd, hatVStart).endVertex(); //TR

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, resourceLocation);
            Tesselator.getInstance().end();

            renderTarget.unbindWrite();
            RenderSystem.disableBlend();

            NativeImage image = makeTransparentScreenshot(renderTarget);

            int x = (index % 10) * 72;
            int y = index / 10 * 72;
            image.copyRect(this.atlas, 0, 0, x, y, 72, 72, false, false);
            this.addGlyph(index);

            try {
                File file = new File("debug_result.png");
                if (!file.exists()) file.createNewFile();
                image.writeToFile(file);
                File atlas = new File("atlas.png");
                if (!atlas.exists()) atlas.createNewFile();
                this.atlas.writeToFile(atlas);
            } catch (Exception e) {
                KapLibMod.LOGGER.warn("error saving result: {}", e.getMessage());
            }

            image.close();
            this.atlasTexture.upload(); //update GPU texture
        });
    }

    /**
     * vanilla's screenshot method clears alpha, therefore I created a new method
     * @see Screenshot#takeScreenshot(RenderTarget)
     */
    private static NativeImage makeTransparentScreenshot(RenderTarget pFrameBuffer) {
        int i = pFrameBuffer.width;
        int j = pFrameBuffer.height;
        NativeImage nativeimage = new NativeImage(i, j, false);
        RenderSystem.bindTexture(pFrameBuffer.getColorTextureId());
        nativeimage.downloadTexture(0, false);
        nativeimage.flipY();
        return nativeimage;
    }

    @Override
    public @NotNull BakedGlyph getGlyph(int pCharacter) {
        if (glyphs[pCharacter] == null) {
            this.addGlyph(pCharacter);
        }
        return glyphs[pCharacter];
    }

    @Override
    public @NotNull BakedGlyph getRandomGlyph(GlyphInfo pGlyph) {
        return glyphs[Mth.nextInt(KapLibMod.RANDOM_SOURCE, 0, this.index)];
    }

    private void addGlyph(int index) {
        int x = (index % 10) * 72;
        int y = index / 10 * 72;
        float atlasWidth = this.atlas.getWidth();
        float atlasHeight = this.atlas.getHeight();
        glyphs[index] = new BakedGlyph(renderTypes, x / atlasWidth, (x + 72) / atlasWidth, y / atlasHeight, (y + 72) / atlasHeight, 0, 8, 2.5f, 11);
    }

    public void init() {
        this.glyphs = new BakedGlyph[25];
        this.atlas = new NativeImage(360, 360, false);
        this.atlasTexture = new DynamicTexture(atlas);
        this.textureManager.register(FONT, this.atlasTexture);
        this.atlasTexture.upload();
        this.lookup.clear();
        this.textLookup.clear();
        index = 0;
        maxIndex = 24;
    }

    //region cache

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void shutDown() {
        if (ClientModConfig.cachePlayerHeads()) {
            File root = new File(KapLibMod.ROOT, "player_heads");
            File image = new File(root, "image.png");
            root.mkdirs();
            try {
                if (!image.exists() && !image.createNewFile()) {
                    LOGGER.warn("unable to create file");
                    return;
                }
                this.atlas.writeToFile(image);
                File data = new File(root, "data.json");
                IOHelper.saveFile(data, CacheData.CODEC, createCacheData());
            } catch (IOException e) {
                LOGGER.warn("unable to save player head data: {}", e.getMessage());
            }
        }
    }

    private CacheData createCacheData() {
        UUID[] data = new UUID[this.index]; //index will be 1 larger than the actual size of the lookup
        for (Map.Entry<UUID, Character> entry : this.lookup.entrySet()) {
            data[entry.getValue()] = entry.getKey();
        }
        return new CacheData(this.maxIndex + 1, List.of(data));
    }

    public void reset() {
        this.atlasTexture.close();
        this.init();
    }

    private record CacheData(int size, List<UUID> players) {
        private static final Codec<CacheData> CODEC = RecordCodecBuilder.create(cacheDataInstance -> cacheDataInstance.group(
                Codec.INT.fieldOf("size").forGetter(CacheData::size),
                UUIDUtil.STRING_CODEC.listOf().fieldOf("players").forGetter(CacheData::players)
        ).apply(cacheDataInstance, CacheData::new));
    }

    public void load() {
        File root = new File(KapLibMod.ROOT, "player_heads");
        if (!root.exists()) {
            this.init();
            return;
        }
        File imageFile = new File(root, "image.png");
        try {
            NativeImage image = NativeImage.read(Files.readAllBytes(imageFile.toPath()));
            if (image.getWidth() % 360 != 0 || image.getHeight() % 360 != 0) {
                LOGGER.warn("unexpected image dimensions: [{}, {}]", image.getWidth(), image.getHeight());
                return;
            }
            this.atlas = image;
            this.atlasTexture = new DynamicTexture(this.atlas);
            this.textureManager.register(FONT, atlasTexture);
            this.atlasTexture.upload();

            File data = new File(root, "data.json");
            DataResult<CacheData> result = CacheData.CODEC.parse(JsonOps.INSTANCE, Streams.parse(new JsonReader(new FileReader(data))));
            result.get().ifLeft(this::copyFrom).ifRight(cacheDataPartialResult -> LOGGER.warn("error loading player heads: {}", cacheDataPartialResult.message()));
        } catch (IOException e) {
            LOGGER.warn("unable to load player heads: {}", e.getMessage());
        }
    }

    private void copyFrom(CacheData cacheData) {
        this.maxIndex = cacheData.size-1;
        this.lookup.clear();
        for (int i = 0; i < cacheData.players.size(); i++) {
            UUID uuid = cacheData.players.get(i);
            this.lookup.put(uuid, (char) i);
        }
        this.glyphs = new BakedGlyph[cacheData.size];
        for (int i = 0; i < maxIndex; i++) {
            this.addGlyph(i);
        }
    }

    //endregion
}