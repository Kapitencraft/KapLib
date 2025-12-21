package net.kapitencraft.kap_lib.requirement.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.requirement.conditions.abstracts.ReqCondition;
import net.kapitencraft.kap_lib.requirement.type.RequirementType;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class RequirementProvider<T> implements DataProvider {
    private final Codec<Map<T, ReqCondition<?>>> codec;
    private final PackOutput output;
    private final String modId;
    private final RequirementType<T> type;

    private final Map<T, ReqCondition<?>> requirements = new HashMap<>();

    protected RequirementProvider(PackOutput output, String modId, RequirementType<T> type) {
        this.output = output;
        this.modId = modId;
        this.type = type;
        this.codec = Codec.unboundedMap(type.serializer().getCodec(), ReqCondition.CODEC);
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

        DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, this.requirements);
        JsonElement element = result.getOrThrow(IllegalArgumentException::new);

        return DataProvider.saveStable(cache, element, target);
    }

    protected abstract void register();

    @Override
    public @NotNull String getName() {
        return this.modId + "-" + this.type.getName() + "-Requirements";
    }
}
