package net.kapitencraft.kap_lib.component.player_head;

import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.authlib.GameProfile;
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
import net.kapitencraft.kap_lib.component.config.ComponentClientModConfig;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.kapitencraft.kap_lib.core.helpers.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
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
    private static final ResourceLocation UNKNOWN = LibConstants.res("textures/font/unknown_player.png");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static PlayerHeadAllocator instance;

    public static final ResourceLocation FONT = LibConstants.res("player_heads");
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
    private NativeImage unknown;
    private NativeImage atlas;
    private DynamicTexture atlasTexture;
    private final Map<UUID, Character> lookup;
    private final Map<UUID, FormattedText> textLookup;
    private int index = 0;
    private int maxIndex = 24;
    private final GlyphRenderTypes renderTypes = GlyphRenderTypes.createForColorTexture(FONT);
    private BakedGlyph[] glyphs;

    //region pending

    private record PendingEntry(UUID uuid, int id) {
    }

    private final List<PendingEntry> pending = new ArrayList<>();

    private void checkPending(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            pending.stream().filter(e -> e.uuid == player.getUUID()).forEach(pendingEntry -> this.register(player.getGameProfile(), pendingEntry.id));
        }
    }
    //endregion

    public PlayerHeadAllocator(SkinManager skinManager, TextureManager manager) {
        super(manager, FONT);
        this.skinManager = skinManager;
        this.textureManager = manager;
        this.gatherDummy();
        this.lookup = new HashMap<>();
        this.textLookup = new HashMap<>();
        this.load();
        NeoForge.EVENT_BUS.addListener(this::checkPending);
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
        if (index >= maxIndex) {
            this.reallocate();
        }
        int index = this.index++;
        LOGGER.debug("attempting to add {} at index {}", uuid, index);

        Minecraft.getInstance().tell(() -> this.addDummySkin(index));
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getPlayerByUUID(uuid) != null) {
            GameProfile profile = level.getPlayerByUUID(uuid).getGameProfile();
            register(profile, index);
        } else pending.add(new PendingEntry(uuid, index));

        return (char) index;
    }

    public void register(GameProfile profile, int id) {
        skinManager.getOrLoad(profile).thenAccept(s ->
                Minecraft.getInstance().tell(() -> this.addSkin(s.texture(), id)));
    }

    private void addDummySkin(int index) {
        this.addGlyph(index);
        this.addPlayerHeadToAtlas(index, this.unknown);
    }

    private void gatherDummy() {
        Minecraft.getInstance().tell(() -> {
            NativeImage nativeimage = new NativeImage(72, 72, false);
            textureManager.getTexture(UNKNOWN).bind();
            nativeimage.downloadTexture(0, false);
            this.unknown = nativeimage;
        });
    }

    private void reallocate() {
        LOGGER.debug("re-allocating player head atlas");
        NativeImage original = this.atlas;
        NativeImage image = new NativeImage(original.getWidth() * 2, original.getHeight(), false);
        original.copyRect(image, 0, 0, 0, 0, original.getWidth(), original.getHeight(), false, false);
        original.close();
        this.atlas = image;
        this.atlasTexture = new DynamicTexture(atlas);
        this.textureManager.register(FONT, atlasTexture);
        BakedGlyph[] oldGlyphs = this.glyphs;
        this.glyphs = new BakedGlyph[oldGlyphs.length * 2];
        this.maxIndex = oldGlyphs.length;
        for (int i = 0; i < this.maxIndex; i++) {
            addGlyph(i);
        }
    }

    private synchronized void addSkin(ResourceLocation resourceLocation, int index) {
        Minecraft.getInstance().execute(() -> {
            RenderTarget renderTarget = new TextureTarget(72, 72, true, false);

            //necessary
            Matrix4f matrix = new Matrix4f();
            matrix.setOrtho(0.0F, 72, 72, 0.0F, -1.0F, 1.0F);
            RenderSystem.setProjectionMatrix(matrix, VertexSorting.ORTHOGRAPHIC_Z);
            RenderSystem.applyModelViewMatrix();

            BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            renderTarget.bindWrite(true);

            RenderSystem.clearColor(0, 1, 1, 0);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true);

            float headStart = 8f / 64f;
            float headEnd = 16f / 64f;

            bufferBuilder.addVertex(4, 4, 0).setUv(headStart, headStart); //TL
            bufferBuilder.addVertex(4, 68, 0).setUv(headStart, headEnd); //BL
            bufferBuilder.addVertex(68, 68, 0).setUv(headEnd, headEnd); //BR
            bufferBuilder.addVertex(68, 4, 0).setUv(headEnd, headStart); //TR

            float hatUStart = 40f / 64f;
            float hatVStart = 8f / 64f;
            float hatUEnd = 48f / 64f;
            float hatVEnd = 16f / 64f;

            bufferBuilder.addVertex(0, 0, 0).setUv(hatUStart, hatVStart); //TL
            bufferBuilder.addVertex(0, 72, 0).setUv(hatUStart, hatVEnd); //BL
            bufferBuilder.addVertex(72, 72, 0).setUv(hatUEnd, hatVEnd); //BR
            bufferBuilder.addVertex(72, 0, 0).setUv(hatUEnd, hatVStart); //TR

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, resourceLocation);

            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

            renderTarget.unbindWrite();
            RenderSystem.disableBlend();

            NativeImage image = makeTransparentScreenshot(renderTarget);

            addPlayerHeadToAtlas(index, image);
            image.close(); //close the screenshot file as it is no longer needed
            Minecraft.getInstance().tell(() -> this.atlasTexture.upload());
        });
    }

    private void addPlayerHeadToAtlas(int index, NativeImage image) {
        int x = (index % 10) * 72;
        image.copyRect(this.atlas, 0, 0, x, 0, 72, 72, false, false);
        //do not close the image as it might be `this.unknown`
        LOGGER.debug("applying index {}", index);
    }

    /**
     * vanilla's screenshot method clears alpha, therefore I created a new method
     *
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
        return glyphs[Mth.nextInt(MathHelper.RANDOM_SOURCE, 0, this.index)];
    }

    private void addGlyph(int index) {
        int x = (index % 10) * 72;
        float atlasWidth = this.atlas.getWidth();
        glyphs[index] = new BakedGlyph(renderTypes, x / atlasWidth, (x + 72) / atlasWidth, 0, 1, 0, 8, -1, 7.5f);
    }

    public void init() {
        this.glyphs = new BakedGlyph[25];
        this.atlas = new NativeImage(1800, 72, false);
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
        if (ComponentClientModConfig.cachePlayerHeads() && this.index > 0) {
            File root = new File(LibConstants.ROOT, "player_heads");
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
            } catch (Exception e) {
                LOGGER.warn("unexpected error saving player head data: {}", e.getMessage());
            }
        }
    }

    private CacheData createCacheData() {
        if (this.index != this.lookup.size())
            LOGGER.warn("position index ({}) should match lookup size ({})", index, lookup.size());
        UUID[] data = new UUID[this.lookup.size()]; //index will be 1 larger than the actual size of the lookup
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
        File root = new File(LibConstants.ROOT, "player_heads");
        if (root.exists()) {
            File imageFile = new File(root, "image.png");
            try {
                NativeImage image = NativeImage.read(Files.readAllBytes(imageFile.toPath()));
                if (image.getWidth() % 1800 == 0 && image.getHeight() == 72) {
                    this.atlas = image;
                    this.atlasTexture = new DynamicTexture(this.atlas);
                    this.textureManager.register(FONT, atlasTexture);
                    this.atlasTexture.upload();

                    File data = new File(root, "data.json");
                    DataResult<CacheData> result = CacheData.CODEC.parse(JsonOps.INSTANCE, Streams.parse(new JsonReader(new FileReader(data))));
                    result.resultOrPartial(w -> LOGGER.warn("error loading player heads: {}", w)).ifPresent(this::copyFrom);
                    return;
                } else {
                    LOGGER.warn("unexpected image dimensions: [{}, {}]", image.getWidth(), image.getHeight());
                }
            } catch (IOException e) {
                LOGGER.warn("unable to load player heads: {}", e.getMessage());
            }
        }
        this.init();
    }

    private void copyFrom(CacheData cacheData) {
        this.maxIndex = cacheData.size - 1;
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