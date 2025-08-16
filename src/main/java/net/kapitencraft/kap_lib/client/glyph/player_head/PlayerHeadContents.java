package net.kapitencraft.kap_lib.client.glyph.player_head;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.util.Optional;
import java.util.UUID;

public class PlayerHeadContents implements ComponentContents {
    public static final MapCodec<PlayerHeadContents> CODEC = UUIDUtil.STRING_CODEC.xmap(PlayerHeadContents::new, PlayerHeadContents::getUuid).fieldOf("value");

    public static final Type<PlayerHeadContents> TYPE = new Type<>(CODEC, "kap_lib:player_head");

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
    public Type<?> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.uuid.toString();
    }
}
