package net.kapitencraft.kap_lib.client.particle.animation.core;

import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.client.particle.animation.modifiers.AnimationElement;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * container of a particle, containing all important information of a particle like <br>
 * x, y & z coordinates, r, g,b & a color values, lifetime & age values
 */
public class ParticleConfig {
    public double x,y,z;
    public float r, g, b, a;
    public int lifeTime, age;
    private final Particle target;

    private int[] elementLengths;
    private int totalLength;

    private final Map<String, Object> properties = new HashMap<>();
    private final List<BiConsumer<ParticleConfig, Integer>> tickers = new ArrayList<>();

    private int tickCount;
    private int elementIndex;
    private int elementStartTick = 0;
    private final ParticleAnimation animation;
    private AnimationElement active;

    public ParticleConfig(Particle target, ParticleAnimation animation) {
        this.target = target;
        this.animation = animation;
        this.init();
    }

    public void sync() {
        target.x = x;
        target.y = y;
        target.z = z;
        target.rCol = r;
        target.gCol = g;
        target.bCol = b;
        target.alpha = a;
        target.setLifetime(lifeTime);
        target.age = age;
    }

    private void init() {
        AnimationElement[] elements = animation.allElements();
        int[] counts = new int[elements.length];
        int totalLength = 0;
        for (int i = 0; i < elements.length; i++) {
            totalLength += counts[i] = elements[i].createLength(this);
        }
        this.elementLengths = counts;
        this.totalLength = totalLength;

        if (elements.length > 0) this.active = elements[0];

        this.r = target.rCol;
        this.g = target.gCol;
        this.b = target.bCol;
        this.a = target.alpha;

        this.x = target.x;
        this.y = target.y;
        this.z = target.z;

        this.lifeTime = target.getLifetime();
    }

    public void tick() {
        if (tickCount - elementStartTick > elementLengths[elementIndex]) {
            elementIndex++;
            active = animation.getElement(elementIndex);
            elementStartTick = tickCount;
        }
        active.tick(this, tickCount - elementStartTick);
        tickers.forEach(c -> c.accept(this, tickCount));
        tickCount++;
        this.sync();
    }

    public void registerTicker(BiConsumer<ParticleConfig, Integer> ticker) {
        this.tickers.add(ticker);
    }

    /**
     * @param element the element to find the percentage of
     * @return the percentage of completion or {@code -1} if it hasn't been started or {@code 2} if it's already completed
     */
    public int completePercentage(AnimationElement element) {
        int elementIndex = CollectionHelper.index(animation.allElements(), element);
        if (elementIndex == -1) throw new IllegalArgumentException("element " + element + " not found inside animation " + animation);
        if (this.elementIndex < elementIndex) return -1;
        else if (this.elementIndex > elementIndex) return 2;
        return activeCompletePercentage();
    }

    /**
     * @return the percentage of completion of the current active element
     */
    public int activeCompletePercentage() {
        return (this.tickCount - elementStartTick) / elementLengths[elementIndex];
    }

    public int remainingTicks() {
        return totalLength - tickCount;
    }

    public boolean hasExpired() {
        return remainingTicks() <= 0;
    }

    public void removeTarget() {
        this.target.remove();
    }

    public void invalidate() {
        this.animation.finalize(this);
        this.sync();
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

    public void setPos(Vec3 vec3) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
    }
}
