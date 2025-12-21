package net.kapitencraft.kap_lib.loot;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class LootContextReader<T> {
    private final LootContext context;
    private @Nullable T value = null;

    private LootContextReader(LootContext context, Class<T> ignored) {
        this.context = context;
    }

    public static <T> LootContextReader<T> simple(LootContext context, Class<T> type, LootContextParam<T> param) {
        return of(context, type).withParam(param);
    }

    public static <T> LootContextReader<T> of(LootContext context, Class<T> type) {
        return new LootContextReader<>(context, type);
    }

    public LootContextReader<T> withParam(LootContextParam<T> param) {
        this.value = context.getParamOrNull(param);
        return this;
    }

    public LootContextReader<T> ifNull(LootContextParam<T> param) {
        this.value = (this.value == null ? context.getParamOrNull(param) : this.value);
        return this;
    }

    public void ifPresent(Consumer<T> toDo) {
        if (getValue() != null) {
            toDo.accept(getValue());
        }
    }

    public boolean isPresent() {
        return getValue() != null;
    }

    public @Nullable T getValue() {
        return value;
    }
}
