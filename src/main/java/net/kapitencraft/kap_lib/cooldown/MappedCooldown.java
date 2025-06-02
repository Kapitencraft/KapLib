package net.kapitencraft.kap_lib.cooldown;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * mapped cooldown. simplifies cooldown gathering for large quantities of objects
 */
public class MappedCooldown<T> {
    private final Map<T, Cooldown> mapped = new HashMap<>();
    private final String path;
    private final Function<T, String> mapper;
    private final @Nullable Consumer<LivingEntity> exe;

    public MappedCooldown(String path, Function<T, String> mapper, @Nullable Consumer<LivingEntity> exe) {
        this.path = path;
        this.mapper = mapper;
        this.exe = exe;
    }

    /**
     * gets the associated Cooldown with the given object
     * @param t the object to get the cooldown for
     * @return the cooldown or null, if no cooldown is applied
     */
    public @Nullable Cooldown get(T t) {
        return mapped.get(t);
    }

    /**
     * @param t the object to get or add
     * @param time the amount in ticks the cooldown should be
     * @return the cooldown, either the already existing one, the one created or null, if time is {@code <= 0}
     */
    public Cooldown getOrCreate(T t, int time) {
        if (time <= 0) return null;
        if (!mapped.containsKey(t)) {
            add(t, time);
        }
        return get(t);
    }

    /**
     * adds the given object with the given time
     * @param t the object to add
     * @param time the time to add
     */
    public void add(T t, int time) {
        CompoundPath path = new CompoundPath(this.path, CompoundPath.COOLDOWN);
        mapped.put(t, new Cooldown(CompoundPath.builder(mapper.apply(t)).withParent(path), time, exe == null ? entity -> {} : exe));
    }
}
