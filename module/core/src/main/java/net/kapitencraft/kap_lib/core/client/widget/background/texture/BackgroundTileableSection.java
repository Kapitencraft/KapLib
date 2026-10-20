package net.kapitencraft.kap_lib.core.client.widget.background.texture;

import com.mojang.serialization.Codec;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public record BackgroundTileableSection(TileType type) {
    private static final Codec<BackgroundTileableSection> CODEC = TileType.CODEC.xmap(BackgroundTileableSection::new, BackgroundTileableSection::type).fieldOf("tile").codec();

    public static final MetadataSectionType<BackgroundTileableSection> SERIALIZER = MetadataSectionType.fromCodec("background", CODEC);

    public enum TileType implements StringRepresentable {
        ONE_BY_ONE,
        TWO_BY_TWO;

        private static final Codec<TileType> CODEC = StringRepresentable.fromEnum(TileType::values);

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

}
