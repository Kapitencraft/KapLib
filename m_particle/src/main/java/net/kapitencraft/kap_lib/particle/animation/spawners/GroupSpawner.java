package net.kapitencraft.kap_lib.particle.animation.spawners;

import com.mojang.serialization.MapCodec;
import net.kapitencraft.kap_lib.particle.animation.core.ParticleSpawnSink;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.SpawnerTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record GroupSpawner(List<Spawner> spawners) implements Spawner {

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void spawn(ParticleSpawnSink sink) {
        for (Spawner spawner : spawners) spawner.spawn(sink);
    }

    @Override
    public @NotNull Type getType() {
        return SpawnerTypes.GROUP.get();
    }

    public static class Builder implements SpawnerBuilder<GroupSpawner> {
        private static final MapCodec<GroupSpawner.Builder> CODEC = Spawner.CODEC.listOf().xmap(GroupSpawner.Builder::new, GroupSpawner.Builder::spawners).fieldOf("entries");

        private final List<Spawner.SpawnerBuilder<?>> spawners = new ArrayList<>();

        private Builder() {}

        private Builder(List<SpawnerBuilder<?>> builders) {
            this.spawners.addAll(builders);
        }

        public Builder addSpawner(SpawnerBuilder<?> spawner) {
            spawners.add(spawner);
            return this;
        }

        private List<SpawnerBuilder<?>> spawners() {
            return spawners;
        }

        @Override
        public GroupSpawner build(ParticleAnimationPresetContext context) {
            return new GroupSpawner(spawners.stream().map(b -> (Spawner) b.build(context)).toList());
        }

        @Override
        public Type type() {
            return SpawnerTypes.GROUP.get();
        }
    }

    public static class Type implements VisibleSpawner.Type<GroupSpawner> {
        private static final StreamCodec<? super RegistryFriendlyByteBuf, GroupSpawner> STREAM_CODEC = Spawner.STREAM_CODEC.apply(ByteBufCodecs.list()).map(GroupSpawner::new, GroupSpawner::spawners);

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, GroupSpawner> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public MapCodec<? extends SpawnerBuilder<GroupSpawner>> codec() {
            return Builder.CODEC;
        }
    }
}
