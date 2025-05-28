package net.kapitencraft.kap_lib.client.glyph.player_head;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.mixin.duck.IKapLibComponentContents;
import net.kapitencraft.kap_lib.registry.custom.ExtraCodecs;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.text.Normalizer;
import java.util.Optional;
import java.util.UUID;

public class PlayerHeadContents implements ComponentContents, IKapLibComponentContents {
    public static final Codec<PlayerHeadContents> CODEC = ExtraCodecs.UUID.xmap(PlayerHeadContents::new, PlayerHeadContents::getUuid);

    private final UUID uuid;

    public PlayerHeadContents(UUID uuid) {
        this.uuid = uuid;
    }

    private UUID getUuid() {
        return this.uuid;
    }

    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> pStyledContentConsumer, Style pStyle) {
        return PlayerHeadAllocator.getInstance().getTextForPlayer(uuid).visit(pStyledContentConsumer, pStyle);
    }

    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> pContentConsumer) {
        return CommonComponents.EMPTY.visit(pContentConsumer);
    }

    @Override
    public Codec<? extends ComponentContents> getCodec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return this.uuid.toString();
    }
}
