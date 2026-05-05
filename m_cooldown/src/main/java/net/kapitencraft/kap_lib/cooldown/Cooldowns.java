package net.kapitencraft.kap_lib.cooldown;


import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.cooldown.network.S2C.CooldownStartedPacket;
import net.kapitencraft.kap_lib.cooldown.network.S2C.SyncCooldownsToPlayerPacket;
import net.kapitencraft.kap_lib.cooldown.registry.CooldownAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * the capability cooldown representation.
 * use {@link Cooldown#applyCooldown(LivingEntity, boolean)} to add cooldowns to entities
 */
@ApiStatus.Internal
public class Cooldowns {
    private static final Codec<AtomicInteger> ATOMIC_INTEGER_CODEC = Codec.INT.xmap(AtomicInteger::new, AtomicInteger::get);
    public static final Codec<Cooldowns> CODEC = Codec.unboundedMap(Cooldown.CODEC, ATOMIC_INTEGER_CODEC).xmap(Cooldowns::new, c -> c.active);

    private final Map<Cooldown, AtomicInteger> active = new HashMap<>();

    private Cooldowns(Map<Cooldown, AtomicInteger> active) {
        this.active.putAll(active);
    }

    public Cooldowns() {
    }

    public boolean isActive(Cooldown cooldown) {
        return active.containsKey(cooldown);
    }

    public void tick(LivingEntity entity) {
        List<Cooldown> toRemove = new ArrayList<>();
        active.forEach((cooldown, integerReference) -> {
            integerReference.decrementAndGet();
            if (integerReference.get() <= 0) toRemove.add(cooldown);
        });
        toRemove.forEach(c -> {
            c.onDone(entity);
            active.remove(c);
        });
    }

    /**
     * apply a cooldown to the entity this capability instance is owned by. must only be called serverside
     *
     * @param cooldown       the cooldown to apply
     * @param reduceWithTime whether {@link net.kapitencraft.kap_lib.cooldown.registry.CooldownAttributes#COOLDOWN_REDUCTION Cooldown Reduction Attribute} should be accounted
     */
    public void applyCooldown(LivingEntity entity, Cooldown cooldown, boolean reduceWithTime) {
        int time = cooldown.getCooldownTime(entity, reduceWithTime);
        if (time > 0) {
            this.active.put(cooldown, new AtomicInteger(time));
            PacketDistributor.sendToAllPlayers(new CooldownStartedPacket(cooldown, time, entity.getId()));
        }
    }

    public int getCooldownTime(Cooldown cooldown) {
        AtomicInteger reference = this.active.get(cooldown);
        return reference == null ? 0 : reference.get();
    }

    public Map<Cooldown, Integer> getData() {
        Map<Cooldown, Integer> map = new HashMap<>();
        this.active.forEach((cooldown, integerReference) -> map.put(cooldown, integerReference.get()));
        return map;
    }

    public void loadData(Map<Cooldown, Integer> map) {
        map.forEach((cooldown, integer) -> this.active.put(cooldown, new AtomicInteger(integer)));
    }

    public static Cooldowns get(LivingEntity living) {
        return Objects.requireNonNull(living.getData(CooldownAttachmentTypes.COOLDOWNS), "unable to get cooldowns");
    }

    public static void send(ServerPlayer sP) {
        Cooldowns cooldown = get(sP);
        PacketDistributor.sendToPlayer(sP, new SyncCooldownsToPlayerPacket(sP.getId(), cooldown.getData()));
    }

    public void setCooldownTime(Cooldown cooldown, int duration) {
        this.active.put(cooldown, new AtomicInteger(duration));
    }
}
