package net.kapitencraft.kap_lib.client.chroma;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * the type how chroma should be rendered
 */
public enum ChromaType implements StringRepresentable, IShaderConfig {
    LINEAR("linear", 1),
    CIRCLE("circle", 0),
    RECTANGLE("rectangle", 2);

    private final String name;
    private final int byteId;

    ChromaType(String name, int byteId) {
        this.name = name;
        this.byteId = byteId;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    @Override
    public int getConfigId() {
        return byteId;
    }
}
