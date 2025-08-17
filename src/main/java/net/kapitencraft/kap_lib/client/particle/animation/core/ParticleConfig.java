package net.kapitencraft.kap_lib.client.particle.animation.core;

import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.client.particle.animation.elements.AnimationElement;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * container of a particle, containing all important information of a particle like <br>
 * x, y & z coordinates, r, g,b & a color values, lifetime & age values
 */
public class ParticleConfig {
    public double x,y,z;
    public double dx, dy, dz;
    public float r, g, b, a;
    public int lifeTime, age;
    private final Particle target;

    private int[] elementLengths;
    private int totalLength;

    /**
     * properties applied to this config. do not persist swapping animation element
     */
    private final Map<String, Object> properties = new HashMap<>();
    private final List<BiConsumer<ParticleConfig, Integer>> tickers = new ArrayList<>();

    /**
     * the current tick count of the Config
     */
    private int tickCount;
    /**
     * the index of the active element inside the animation
     */
    private int elementIndex;
    /**
     * the tick the current active element started in
     */
    private int elementStartTick = 0;
    /**
     * the animation this config is a part of
     */
    private final ParticleAnimation animation;
    /**
     * the currently active element
     */
    private AnimationElement active;

    @ApiStatus.Internal
    public ParticleConfig(Particle target, ParticleAnimation animation) {
        this.target = target;
        this.animation = animation;
        this.init();
    }

    /**
     * synchronizes the underlying particle to any changes mode to this config
     */
    @ApiStatus.Internal
    public void sync() {
        target.x = x;
        target.y = y;
        target.z = z;
        target.rCol = r;
        target.gCol = g;
        target.bCol = b;
        target.alpha = a;
        target.setLifetime(lifeTime);
        target.xd = dx;
        target.yd = dy;
        target.zd = dz;
        target.age = age;
    }

    /**
     * initializes the configuration.
     */
    @ApiStatus.Internal
    private void init() {
        List<AnimationElement> elements = animation.allElements();
        int[] counts = new int[elements.size()];
        int totalLength = 0;
        for (int i = 0; i < elements.size(); i++) {
            totalLength += counts[i] = elements.get(i).createLength(this);
        }
        this.elementLengths = counts;
        this.totalLength = totalLength;

        //Update properties before init
        this.r = target.rCol;
        this.g = target.gCol;
        this.b = target.bCol;
        this.a = target.alpha;

        this.x = target.x;
        this.y = target.y;
        this.z = target.z;

        this.lifeTime = target.getLifetime();

        if (!elements.isEmpty()) {
            this.active = elements.getFirst();
            this.active.initialize(this);
        }

    }

    @ApiStatus.Internal
    public void tick() {
        if (tickCount - elementStartTick >= elementLengths[elementIndex]) {
            elementIndex++;
            active.finalize(this);
            active = animation.getElement(elementIndex);
            properties.clear();
            active.initialize(this);
            elementStartTick = tickCount;
        }
        int currentTickCount = tickCount - elementStartTick;
        active.tick(this, currentTickCount, (double) currentTickCount / (elementLengths[elementIndex] - 1));
        tickers.forEach(c -> c.accept(this, tickCount));
        tickCount++;
        this.sync();
    }

    /**
     * register a ticker to this config.
     * tickers will be called (you guessed it) each tick
     */
    public void registerTicker(BiConsumer<ParticleConfig, Integer> ticker) {
        this.tickers.add(ticker);
    }

    /**
     * @param element the element to find the percentage of
     * @return the percentage of completion or {@code -1} if it hasn't been started or {@code 2} if it's already completed
     */
    public int completePercentage(AnimationElement element) {
        int elementIndex = animation.allElements().indexOf(element);
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

    /**
     * @return the amount of ticks this config has until the animation's particle finalizer will be called for this config
     */
    public int remainingTicks() {
        return totalLength - tickCount;
    }

    /**
     * @return whether the config has completed all animation elements or not
     */
    public boolean hasExpired() {
        return remainingTicks() <= 0;
    }

    /**
     * removes the target particle
     */
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

    @SuppressWarnings("unchecked")
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


    public void setPos(Vec3 vec3) {
        this.x = vec3.x;
        this.y = vec3.y;
        this.z = vec3.z;
    }
}
