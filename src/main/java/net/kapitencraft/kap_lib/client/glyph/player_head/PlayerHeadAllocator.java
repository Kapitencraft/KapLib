package net.kapitencraft.kap_lib.client.glyph.player_head;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.kapitencraft.kap_lib.KapLibMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PlayerHeadAllocator {
    public static final ResourceLocation FONT = KapLibMod.res("player_heads");
    private final SkinManager skinManager;

    public PlayerHeadAllocator(SkinManager skinManager) {
        this.skinManager = skinManager;
    }

    public static PlayerHeadAllocator getInstance() {
        return null;
    }


    public char getGlyphForPlayer(UUID player) {

    }

    public void addPlayer(UUID uuid) {
        GameProfile profile = new GameProfile(uuid, null);
        skinManager.registerSkins(profile, (type, resourceLocation, minecraftProfileTexture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                this.addSkin(resourceLocation);
            }
        }, false);
        skinManager.getInsecureSkinLocation(profile);
    }

    private void addSkin(ResourceLocation resourceLocation) {
        Minecraft.getInstance().execute(() -> {
            RenderTarget renderTarget = new TextureTarget();
            RenderSystem.setShaderTexture(0, resourceLocation);
            BufferBuilder bufferBuilder = new BufferBuilder(8);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            bufferBuilder.vertex();
        });
    }

    public static MutableComponent create(UUID playerId) {
        return MutableComponent.create(new PlayerHeadContents(playerId));
    }
}
