package net.kapitencraft.kap_lib.client.glyph.player_head;

import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.text.Format;
import java.util.Optional;
import java.util.UUID;

public class PlayerHeadContents implements ComponentContents {
    private final FormattedText text;

    public PlayerHeadContents(UUID uuid) {
        this.text = FormattedText.of(String.valueOf(PlayerHeadAllocator.getInstance().getGlyphForPlayer(uuid)), Style.EMPTY.withFont(PlayerHeadAllocator.FONT));
    }

    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> pStyledContentConsumer, Style pStyle) {
        return text.visit(pStyledContentConsumer, pStyle);
    }

    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> pContentConsumer) {
        return text.visit(pContentConsumer);
    }
}
