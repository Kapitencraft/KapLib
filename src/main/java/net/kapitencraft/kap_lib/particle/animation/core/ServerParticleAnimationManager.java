package net.kapitencraft.kap_lib.particle.animation.core;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.kapitencraft.kap_lib.core.io.JsonHelper;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPreset;
import net.kapitencraft.kap_lib.particle.animation.store.ParticleAnimationPresetContext;
import net.kapitencraft.kap_lib.particle.network.S2C.ActivateParticleAnimationsPacket;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.io.File;
import java.util.*;

public class ServerParticleAnimationManager extends SimpleJsonResourceReloadListener {
    private static final File STORAGE = new File(LibConstants.ROOT, "animations.json");
    private static final Codec<List<Entry>> CODEC = Entry.CODEC.listOf();

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ServerParticleAnimationManager INSTANCE = new ServerParticleAnimationManager();

    private final Map<ResourceLocation, ParticleAnimationPreset> presets = new HashMap<>();
    private final List<Entry> animations = new ArrayList<>();

    public ServerParticleAnimationManager() {
        super(JsonHelper.GSON, "animation_presets");
        this.load();
        NeoForge.EVENT_BUS.addListener(this::handleServerStop);
        NeoForge.EVENT_BUS.addListener(this::handlePlayerJoin);
    }

    public static void accept(ParticleAnimationPreset animation, ServerPlayer target, Map<String, UUID> context) {
        INSTANCE.animations.add(Entry.single(animation, target, context));
    }

    public static void accept(ParticleAnimationPreset animation, Map<String, UUID> context) {
        INSTANCE.animations.add(Entry.all(animation, context));
    }

    private void store() {
        IOHelper.saveFile(STORAGE, CODEC, this.animations);
    }

    private void load() {
        this.animations.addAll(IOHelper.loadFile(STORAGE, CODEC, List::of));
    }

    private void handleServerStop(ServerStoppingEvent event) {
        this.store();
    }

    private void handlePlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        this.activeAnimations((ServerPlayer) event.getEntity());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        presets.clear();
        object.forEach((resourceLocation, jsonElement) -> {
            if (jsonElement.isJsonObject()) {
                try {
                    DataResult<Pair<ParticleAnimationPreset, JsonElement>> result = ParticleAnimationPreset.CODEC.decode(JsonOps.INSTANCE, jsonElement);
                    this.presets.put(resourceLocation, result.getOrThrow().getFirst());
                } catch (Exception e) {
                    LOGGER.warn("unable to load preset {}: {}", resourceLocation, e.getMessage());
                }
            }
            LOGGER.warn("unable to load preset {}: not a json object", resourceLocation);
        });
    }

    public void activeAnimations(ServerPlayer player) {
        List<ParticleAnimation> toActivate = new ArrayList<>();
        for (Entry animationEntry : this.animations) {
            if (animationEntry.targets.isEmpty() || animationEntry.targets.contains(player.getUUID()))
                toActivate.add(animationEntry.animation.build(ParticleAnimationPresetContext.EMPTY));
        }
        PacketDistributor.sendToPlayer(player, new ActivateParticleAnimationsPacket(toActivate));
    }

    private record Entry(Set<UUID> targets, ParticleAnimationPreset animation, Map<String, UUID> context) {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                UUIDUtil.STRING_CODEC.listOf().xmap(Set::copyOf, List::copyOf).fieldOf("targets").forGetter(Entry::targets),
                ParticleAnimationPreset.CODEC.fieldOf("animation").forGetter(Entry::animation),
                Codec.unboundedMap(Codec.STRING, UUIDUtil.STRING_CODEC).fieldOf("context").forGetter(Entry::context)
        ).apply(i, Entry::new));

        public static Entry single(ParticleAnimationPreset animation, ServerPlayer target, Map<String, UUID> params) {
            return new Entry(Set.of(target.getUUID()), animation, params);
        }

        public static Entry all(ParticleAnimationPreset animation, Map<String, UUID> params) {
            return new Entry(Set.of(), animation, params);
        }
    }
}
