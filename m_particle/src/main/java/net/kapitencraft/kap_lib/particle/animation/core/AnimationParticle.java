package net.kapitencraft.kap_lib.particle.animation.core;

import net.kapitencraft.kap_lib.particle.animation.elements.AnimationElement;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AnimationParticle extends SingleQuadParticle {

    private TextureAtlasSprite texture;
    private ParticleRenderType renderType;

    private final List<BiConsumer<ParticleData, Integer>> tickers = new ArrayList<>();

    private final ParticleData config;
    private final int[] elementLengths;
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

    protected AnimationParticle(ClientLevel level, ParticleAnimation animation, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.config = new ParticleData(this);
        this.animation = animation;

        List<AnimationElement> elements = animation.allElements();
        int[] counts = new int[elements.size()];
        int totalLength = 0;
        for (int i = 0; i < elements.size(); i++) {
            totalLength += counts[i] = elements.get(i).createLength(this.config);
        }
        this.elementLengths = counts;
        this.lifetime = totalLength;
    }

    /**
     * @param element the element to find the percentage of
     * @return the percentage of completion or {@code -1} if it hasn't been started or {@code 2} if it's already completed
     */
    public int completePercentage(AnimationElement element) {
        int elementIndex = animation.allElements().indexOf(element);
        if (elementIndex == -1)
            throw new IllegalArgumentException("element " + element + " not found inside animation " + animation);
        if (this.elementIndex < elementIndex) return -1;
        else if (this.elementIndex > elementIndex) return 2;
        return activeCompletePercentage();
    }

    /**
     * @return the percentage of completion of the current active element
     */
    public int activeCompletePercentage() {
        return (this.age - elementStartTick) / elementLengths[elementIndex];
    }

    /**
     * @return the amount of ticks this config has until the animation's particle finalizer will be called for this config
     */
    public int remainingTicks() {
        return lifetime - age;
    }

    /**
     * register a ticker to this config.
     * tickers will be called (you guessed it) each tick
     */
    public void registerTicker(BiConsumer<ParticleData, Integer> ticker) {
        this.tickers.add(ticker);
    }

    @Override
    public void tick() {
        if (age - elementStartTick >= elementLengths[elementIndex]) {
            elementIndex++;
            active.finalize(this.config);
            active = animation.getElement(elementIndex);
            active.initialize(this.config);
            elementStartTick = age;
        }
        int currentTickCount = age - elementStartTick;
        active.tick(this.config, currentTickCount, (double) currentTickCount / (elementLengths[elementIndex] - 1));
        tickers.forEach(c -> c.accept(this.config, age));
        if (this.age++ >= this.lifetime) {
            this.invalidate();
        }
        this.config.sync();
    }

    @Override
    protected float getU0() {
        return texture.getU0();
    }

    @Override
    protected float getU1() {
        return texture.getU1();
    }

    @Override
    protected float getV0() {
        return texture.getV0();
    }

    @Override
    protected float getV1() {
        return texture.getV1();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return renderType;
    }

    public void invalidate() {
        this.animation.finalize(this.config);
        this.remove();
    }

    public void setQuadSize(float size) {
        this.quadSize = size;
    }

    public boolean isDead() {
        return age >= lifetime;
    }

    public float getQuadSize() {
        return this.quadSize;
    }

    public void setTexture(TextureStorage.StorageEntry entry) {
        this.texture = entry.sprite();
        this.renderType = entry.type();
    }
}
