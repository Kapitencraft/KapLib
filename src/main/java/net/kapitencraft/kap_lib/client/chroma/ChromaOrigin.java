package net.kapitencraft.kap_lib.client.chroma;

import net.minecraft.util.StringRepresentable;

/**
 * the origin (or alignment) of the Chroma
 * <br> (from which point it's animated from
 * <br> use as example
 */
public enum ChromaOrigin implements IShaderConfig, StringRepresentable {
    BOTTOM_LEFT("bottom_left"),
    TOP_LEFT("top_left"),
    BOTTOM_RIGHT("bottom_right"),
    TOP_RIGHT("top_right");

    private final String name;

    ChromaOrigin(String name) {
        this.name = name;
    }

    @Override
    public int getConfigId() {
        return ordinal();
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
