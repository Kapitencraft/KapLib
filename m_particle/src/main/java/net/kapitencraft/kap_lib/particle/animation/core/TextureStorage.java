package net.kapitencraft.kap_lib.particle.animation.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.helpers.ExtraStreamCodecs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TextureStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("BonusManager");

    private final Map<String, StorageEntry> entries;

    public TextureStorage(Map<String, StorageEntry> entries) {
        this.entries = entries;
    }

    public StorageEntry get(String name) {
        return entries.get(name);
    }

    public record StorageEntry(TextureAtlasSprite sprite, ParticleRenderType type) {
    }

    private static class CustomParticleRenderType implements ParticleRenderType {
        private final boolean translucent;
        private final ResourceLocation atlasLocation;

        public CustomParticleRenderType(ResourceLocation atlas, boolean b) {
            this.atlasLocation = atlas;
            this.translucent = b;
        }

        @Override
        public @Nullable BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            if (translucent) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            } else {
                RenderSystem.disableBlend();
                RenderSystem.setShader(GameRenderer::getParticleShader);
            }
            RenderSystem.depthMask(true);
            RenderSystem.setShaderTexture(0, atlasLocation);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
    }

    public static class StorageDraft {
        public static final Codec<StorageDraft> CODEC = Codec.unboundedMap(Codec.STRING, DraftEntry.CODEC).xmap(StorageDraft::new, d -> d.entries);
        public static final StreamCodec<FriendlyByteBuf, StorageDraft> STREAM_CODEC = DraftEntry.STREAM_CODEC.apply(ExtraStreamCodecs.map(ByteBufCodecs.STRING_UTF8)).map(StorageDraft::new, d -> d.entries);

        private final Map<String, DraftEntry> entries;

        private StorageDraft(Map<String, DraftEntry> entries) {
            this.entries = entries;
        }

        public TextureStorage build() {
            ImmutableMap.Builder<String, StorageEntry> builder = new ImmutableMap.Builder<>();
            this.entries.forEach((s, draftEntry) -> {
                StorageEntry build = draftEntry.build();
                if (build != null)
                    builder.put(s, build);
            });
            return new TextureStorage(builder.build());
        }

        public record DraftEntry(ResourceLocation atlasLocation, ResourceLocation textureLocation,
                                  boolean translucent) {
            public static final DraftEntry FLAME = new DraftEntry(TextureAtlas.LOCATION_PARTICLES, ResourceLocation.withDefaultNamespace("flame"), false);

            public static final Codec<DraftEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                    ResourceLocation.CODEC.fieldOf("atlas").forGetter(DraftEntry::atlasLocation),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(DraftEntry::textureLocation),
                    Codec.BOOL.optionalFieldOf("translucent", true).forGetter(DraftEntry::translucent)
            ).apply(i, DraftEntry::new));
            public static final StreamCodec<FriendlyByteBuf, DraftEntry> STREAM_CODEC = StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, DraftEntry::atlasLocation,
                    ResourceLocation.STREAM_CODEC, DraftEntry::textureLocation,
                    ByteBufCodecs.BOOL, DraftEntry::translucent,
                    DraftEntry::new
            );

            public StorageEntry build() {
                try {
                    ParticleRenderType renderType;
                    if (atlasLocation.equals(InventoryMenu.BLOCK_ATLAS)) {
                        if (translucent) {
                            renderType = ParticleRenderType.TERRAIN_SHEET;
                        } else {
                            renderType = new CustomParticleRenderType(atlasLocation, false);
                        }
                    } else if (atlasLocation.equals(TextureAtlas.LOCATION_PARTICLES)) {
                        if (translucent) {
                            renderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
                        } else {
                            renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
                        }
                    } else {
                        renderType = new CustomParticleRenderType(atlasLocation, translucent);
                    }
                    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(atlasLocation).apply(textureLocation);
                    return new StorageEntry(sprite, renderType);
                } catch (Exception e) {
                    LOGGER.warn("error loading texture entry for '{}': {}", textureLocation, e.getMessage());
                }
                return null;
            }
        }

        public static class Builder {
            private final ImmutableMap.Builder<String, DraftEntry> entries = new ImmutableMap.Builder<>();

            public Builder addEntry(String name, ResourceLocation atlasLocation, ResourceLocation textureLocation, boolean translucent) {
                this.entries.put(name, new DraftEntry(atlasLocation, textureLocation, translucent));
                return this;
            }

            public StorageDraft build() {
                return new StorageDraft(entries.build());
            }

            public void addEntry(String name, DraftEntry entry) {
                this.entries.put(name, entry);
            }
        }
    }
}
