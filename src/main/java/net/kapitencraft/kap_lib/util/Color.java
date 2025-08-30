package net.kapitencraft.kap_lib.util;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Range;

public record Color(float r, float g, float b, float a) {
    public static final Codec<Color> CODEC = Codec.INT.xmap(Color::fromARGBPacked, Color::pack);

    /**
     * color from r, g, b and a values
     */
    public Color {
    }

    @SuppressWarnings("DataFlowIssue")
    public static Color fromFormatting(ChatFormatting formatting) {
        return fromARGBPacked(formatting.getColor());
    }

    public static Color fromARGBPacked(int packed) {
        return new Color(
                (packed >> 16 & 255) / 255f,
                (packed >> 8 & 255) / 255f,
                (packed & 255) / 255f,
                (packed >> 24 & 255) / 255f
        );
    }

    /**
     * scales all colors
     */
    public Color mul(float scale) {
        return new Color(r * scale, g * scale, b * scale, a * scale);
    }

    public Color add(Color other) {
        return new Color(this.r + other.r, this.g + other.g, this.b + other.b, this.a + other.a);
    }

    public Color setAlpha(float alpha) {
        return new Color(this.r, this.g, this.b, alpha);
    }

    public Color merge(Color other) {
        return add(other).mul(.5f);
    }

    /**
     * packs this color into 32-bit ARGB integer
     */
    public int pack() {
        return MathHelper.ARGBtoInt((int) (this.a * 255), (int) (this.r * 255), (int) (this.g * 255), (int) (this.b * 255));
    }


    public Color mix(Color other, @Range(from = 0, to = 1) float percentage) {
        return other.mul(1 - percentage).add(this.mul(percentage));
    }

    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.pack());
    }

    public static Color read(FriendlyByteBuf buf) {
        return Color.fromARGBPacked(buf.readInt());
    }

    public TextColor toTextColor() {
        return TextColor.fromRgb(this.pack());
    }
}
