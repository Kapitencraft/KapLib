package net.kapitencraft.kap_lib.data_gen.abst;

import com.google.gson.JsonObject;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.Markers;
import net.kapitencraft.kap_lib.requirements.type.RegistryReqType;
import net.kapitencraft.kap_lib.requirements.conditions.abstracts.ReqCondition;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class RequirementProvider<T> implements DataProvider {
    private final PackOutput output;
    private final String modId;
    private final RegistryReqType<T> type;

    private final Map<T, ReqCondition<?>> requirements = new HashMap<>();

    protected RequirementProvider(PackOutput output, String modId, RegistryReqType<T> type) {
        this.output = output;
        this.modId = modId;
        this.type = type;
    }

    protected void add(T element, ReqCondition<?> condition) {
        this.requirements.put(element, condition);
    }

    protected void add(Supplier<T> supplier, ReqCondition<?> condition) {
        this.add(supplier.get(), condition);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        register();
        if (!requirements.isEmpty()) {
            return save(pOutput, this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modId).resolve("requirements").resolve(this.type.getName() + ".json"));
        }

        return CompletableFuture.allOf();
    }

    private CompletableFuture<?> save(CachedOutput cache, Path target) {
        JsonObject json = new JsonObject();

        this.requirements.forEach((t, condition) -> {
            ResourceLocation elementId = this.type.getId(t);
            if (elementId == null) {
                KapLibMod.LOGGER.warn(Markers.REQUIREMENTS_MANAGER, "could not find element {} in requirement type '{}'; skipping!", t.getClass().getCanonicalName(), this.type.getName());
                return;
            }
            json.add(elementId.toString(), condition.toJson());
        });

        return DataProvider.saveStable(cache, json, target);
    }

    protected abstract void register();

    @Override
    public @NotNull String getName() {
        return this.modId + "-" + this.type.getName() + "-Requirements";
    }
}
