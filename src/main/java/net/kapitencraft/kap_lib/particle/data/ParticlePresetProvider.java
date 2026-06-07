package net.kapitencraft.kap_lib.particle.data;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleAnimation;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPreset;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ParticlePresetProvider implements DataProvider {
    private final List<Pair<ResourceLocation, ParticleAnimationPreset>> presets = new ArrayList<>();
    private final PackOutput output;
    private final PackOutput.Target dist;

    public ParticlePresetProvider(PackOutput output, PackOutput.Target dist) {
        this.output = output;
        this.dist = dist;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        this.presets.clear();
        register();
        List<CompletableFuture<?>> tasks = new ArrayList<>();
        for (Pair<ResourceLocation, ParticleAnimationPreset> preset : this.presets) {
            Path path = this.output.createPathProvider(dist, "animation_presets").file(preset.getFirst(), "json");
            JsonElement content = ParticleAnimationPreset.CODEC.encodeStart(JsonOps.INSTANCE, preset.getSecond()).getOrThrow();
            tasks.add(DataProvider.saveStable(cachedOutput, content, path));
        }
        return CompletableFuture.allOf(tasks.toArray(CompletableFuture[]::new));
    }

    public abstract void register();

    protected void add(ResourceLocation location, ParticleAnimation.ParticleAnimationBuilder builder) {
        this.presets.add(Pair.of(location, builder.toPreset()));
    }

    @Override
    public String getName() {
        return "Particle Preset Provider " + dist.name();
    }
}
