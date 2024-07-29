package net.kapitencraft.kap_lib.data_gen;

import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.requirements.RequirementType;
import net.kapitencraft.kap_lib.requirements.type.abstracts.CountCondition;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class RequirementProvider<T> implements DataProvider {
    private final PackOutput output;
    private final String modId;
    private final RequirementType<T> type;

    private Map<T, CountCondition<?>> requirements = new HashMap<>();

    protected RequirementProvider(PackOutput output, String modId, RequirementType<T> type) {
        this.output = output;
        this.modId = modId;
        this.type = type;
    }

    protected void add(T element, CountCondition<?> condition) {
        this.requirements.put(element, condition);
    }

    @Override
    public @NotNull CompletableFuture<?> run(CachedOutput pOutput) {
        register();
        if (requirements != null) {
            return save(pOutput, this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modId).resolve("requirements").resolve(this.type.getName() + ".json"));
        }

        return CompletableFuture.allOf();
    }

    private CompletableFuture<?> save(CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();

        this.requirements.forEach((t, condition) -> {
            ResourceLocation elementId = this.type.getReg().getKey(t);
            if (elementId == null) {
                KapLibMod.LOGGER.warn(Markers.REQUIREMENTS_MANAGER, "could not find element {} in registry '{}'; skipping!", t.getClass().getCanonicalName(), this.type.getReg().getRegistryName());
                return;
            }
            json.add(elementId.toString(), condition.saveToJson());
        });

        return DataProvider.saveStable(cache, json, target);
    }

    protected abstract void register();

    @Override
    public String getName() {
        return this.modId + "-" + this.type.getName() + "-Requirements";
    }
}