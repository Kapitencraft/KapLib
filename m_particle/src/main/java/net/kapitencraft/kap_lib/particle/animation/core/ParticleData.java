package net.kapitencraft.kap_lib.particle.animation.core;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * container of a particle, containing all important information of a particle like <br>
 * x, y and z coordinates, r, g, b and a color values, lifetime and age values
 */
public class ParticleData {
    public double x, y, z;
    public double dx, dy, dz;
    public float r, g, b, a;
    public int lifeTime, age;
    public float size;
    private final AnimationParticle target;

    private final Map<String, Object> properties = new HashMap<>();

    @ApiStatus.Internal
    public ParticleData(AnimationParticle target) {
        this.target = target;
        this.init();
    }

    /**
     * synchronizes the underlying particle to any changes mode to this config
     */
    @ApiStatus.Internal
    public void sync() {
        target.setPos(x, y, z);
        target.rCol = r;
        target.gCol = g;
        target.bCol = b;
        target.alpha = a;
        target.setQuadSize(size);
        target.setLifetime(lifeTime);
        target.setParticleSpeed(dx, dy, dz);
        target.age = age;
    }

    /**
     * initializes the configuration.
     */
    @ApiStatus.Internal
    private void init() {
        //Update properties before init
        this.r = target.rCol;
        this.g = target.gCol;
        this.b = target.bCol;
        this.a = target.alpha;

        Vec3 pos = target.getPos();
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        this.lifeTime = target.getLifetime();
        this.size = target.getQuadSize();
    }

    /**
     * removes the target particle
     */
    public void removeTarget() {
        this.target.remove();
    }

    public Vec3 pos() {
        return new Vec3(this.x, this.y, this.z);
    }

    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }

    public <T> T getOrCreateProperty(String key, Supplier<T> value) {
        if (!properties.containsKey(key)) properties.put(key, value.get());
        return getProperty(key);
    }

    public <T> void setProperty(String key, T value) {
        properties.put(key, value);
    }

    public void setTexture(TextureStorage.StorageEntry entry) {
        this.target.setTexture(entry);
    }

    public void setPos(Vec3 vec3) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
    }
}
