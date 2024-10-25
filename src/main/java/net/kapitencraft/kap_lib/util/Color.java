package net.kapitencraft.kap_lib.util;

import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Range;

public class Color {
    public static final Codec<Color> CODEC = Codec.INT.xmap(Color::new, Color::pack);

    public final float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(ChatFormatting color) {
        this(color.getColor());
    }

    public Color(int packed) {
        this.a = (packed >> 24 & 255) / 255f;
        this.r = (packed >> 16 & 255) / 255f;
        this.g = (packed >> 8 & 255) / 255f;
        this.b = (packed & 255) / 255f;
    }

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

    public int pack() {
        return MathHelper.RGBAtoInt((int) (this.r * 255), (int) (this.g * 255), (int) (this.b * 255), (int) (this.a * 255));
    }


    public Color mix(Color other, @Range(from = 0, to = 1) float percentage) {
        return other.mul(1 - percentage).add(this.mul(percentage));
    }

    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.pack());
    }

    public static Color read(FriendlyByteBuf buf) {
        return new Color(buf.readInt());
    }

    public TextColor toTextColor() {
        return TextColor.fromRgb(this.pack());
    }
}
