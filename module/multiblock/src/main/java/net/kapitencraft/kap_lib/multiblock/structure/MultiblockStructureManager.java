package net.kapitencraft.kap_lib.multiblock.structure;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.core.helpers.TextHelper;
import net.kapitencraft.kap_lib.multiblock.structure.match.MultiblockStructureMatch;
import net.kapitencraft.kap_lib.multiblock.structure.match.MultiblockStructurePattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MultiblockStructureManager {
    public static final Logger LOGGER = LoggerFactory.getLogger("MultiblockStructureManager");

    private Multimap<BlockState, StructurePositionEntry> entries;
    private MultiblockStructurePatternManager patternManager;
    private final List<Pair<ResourceKey<Level>, MultiblockStructureMatch>> pendingMatches = new ArrayList<>();

    public static final MultiblockStructureManager INSTANCE = new MultiblockStructureManager();

    public MultiblockStructureManager() {
        NeoForge.EVENT_BUS.register(this);
    }

    public void rebuild(MultiblockStructurePatternManager patternManager) {
        ImmutableMultimap.Builder<BlockState, StructurePositionEntry> builder = new ImmutableMultimap.Builder<>();
        Map<ResourceLocation, MultiblockStructurePattern> all = patternManager.getAll();
        all.forEach((resourceLocation, multiblockStructurePattern) -> {
            Multimap<BlockState, Vec3i> positions = multiblockStructurePattern.buildInfo();
            positions.forEach((blockState, vec3i) -> {
                builder.put(blockState, new StructurePositionEntry(resourceLocation, vec3i));
            });
        });
        this.entries = builder.build();
        this.patternManager = patternManager;
    }

    public void notifyChange(LevelAccessor level, BlockSnapshot pos, BlockState newState) {
        if (entries.containsKey(newState)) {
            ProfilerFiller profiler = null;
            if (level instanceof Level l) {
                profiler = l.getProfiler();
            }
            Collection<StructurePositionEntry> structurePositions = entries.get(newState);
            if (profiler != null) {
                profiler.push("scanning multiblock match");
            }
            for (StructurePositionEntry structurePosition : structurePositions) {
                MultiblockStructurePattern pattern = this.patternManager.getStructure(structurePosition.structureLocation);
                List<BlockPos> origins = pattern.findPotentialOrigins(level, pos.getPos(), structurePosition.posInStructure);
                for (BlockPos origin : origins) {
                    MultiblockStructureMatch match = pattern.match(level, origin);
                    if (match != null) {
                        LOGGER.debug("found new multiblock structure of type {} at origin {}, size {}",
                                structurePosition.structureLocation, origin.toDimView(),
                                match.getSize().toDimView()
                        );
                        pendingMatches.add(Pair.of(pos.getDimension(), match));
                    }
                }
            }
            if (profiler != null)
                profiler.pop();
        }
    }

    private record StructurePositionEntry(ResourceLocation structureLocation, Vec3i posInStructure) {
    }

    @SubscribeEvent
    private void handlePlaceStructures(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ProfilerFiller profiler = server.getProfiler();
        if (pendingMatches.isEmpty())
            return;
        profiler.push("placing multiblock structure");
        for (Iterator<Pair<ResourceKey<Level>, MultiblockStructureMatch>> iterator = this.pendingMatches.iterator(); iterator.hasNext(); ) {
            Pair<ResourceKey<Level>, MultiblockStructureMatch> pendingMatch = iterator.next();
            pendingMatch.getSecond().place(server.getLevel(pendingMatch.getFirst()));
            iterator.remove();
        }
        profiler.pop();
    }
}
