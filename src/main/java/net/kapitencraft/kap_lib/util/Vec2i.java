package net.kapitencraft.kap_lib.util;

/**
 * 2d vector for integers
 */
public class Vec2i {

    public final int x, y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i(int val) {
        this(val, val);
    }

    public Vec2i add(int x, int y) {
        return new Vec2i(this.x + x, this.y + y);
    }

    public Vec2i sub(int x, int y) {
        return new Vec2i(this.x - x, this.y - y);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vec2i vec2i && vec2i.x == this.x && vec2i.y == this.y;
    }
}
