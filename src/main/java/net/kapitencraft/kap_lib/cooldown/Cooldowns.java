package net.kapitencraft.kap_lib.cooldown;


import com.mojang.serialization.Codec;
import net.kapitencraft.kap_lib.io.network.S2C.capability.CooldownStartedPacket;
import net.kapitencraft.kap_lib.io.network.S2C.capability.SyncCooldownsToPlayerPacket;
import net.kapitencraft.kap_lib.registry.ModAttachmentTypes;
import net.kapitencraft.kap_lib.util.IntegerReference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;


/**
 * the capability cooldown representation. does not need to be modified outside of this library
 */
@ApiStatus.Internal
public class Cooldowns {
    public static final Codec<Cooldowns> CODEC = Codec.unboundedMap(Cooldown.CODEC, IntegerReference.CODEC).xmap(Cooldowns::new, c -> c.active);

    private final Map<Cooldown, IntegerReference> active = new HashMap<>();

    private Cooldowns(Map<Cooldown, IntegerReference> active) {
        this.active.putAll(active);
    }

    public Cooldowns() {}

    public boolean isActive(Cooldown cooldown) {
        return active.containsKey(cooldown);
    }

    public void tick(LivingEntity entity) {
        List<Cooldown> toRemove = new ArrayList<>();
        active.forEach((cooldown, integerReference) -> {
            integerReference.decrease();
            if (integerReference.getIntValue() <= 0) toRemove.add(cooldown);
        });
        toRemove.forEach(c -> {
            c.onDone(entity);
            active.remove(c);
        });
    }

    /**
     * apply a cooldown to the entity this capability instance is owned by. must only be called serverside
     * @param cooldown the cooldown to apply
     * @param reduceWithTime whether {@link net.kapitencraft.kap_lib.registry.ExtraAttributes#COOLDOWN_REDUCTION} should be accounted
     */
    public void applyCooldown(LivingEntity entity, Cooldown cooldown, boolean reduceWithTime) {
        int time = cooldown.getCooldownTime(entity, reduceWithTime);
        if (time > 0) {
            this.active.put(cooldown, IntegerReference.create(time));
            PacketDistributor.sendToAllPlayers(new CooldownStartedPacket(cooldown, time, entity.getId()));
        }
    }

    public int getCooldownTime(Cooldown cooldown) {
        IntegerReference reference = this.active.get(cooldown);
        return reference == null ? 0 : reference.getIntValue();
    }

    public Map<Cooldown, Integer> getData() {
        Map<Cooldown, Integer> map = new HashMap<>();
        this.active.forEach((cooldown, integerReference) -> map.put(cooldown, integerReference.getIntValue()));
        return map;
    }

    public void loadData(Map<Cooldown, Integer> map) {
        map.forEach((cooldown, integer) -> this.active.put(cooldown, IntegerReference.create(integer)));
    }

    public static Cooldowns get(LivingEntity living) {
        return Objects.requireNonNull(living.getData(ModAttachmentTypes.COOLDOWNS), "unable to get cooldowns");
    }


    public static void send(ServerPlayer sP) {
        Cooldowns cooldown = get(sP);
        PacketDistributor.sendToPlayer(sP, new SyncCooldownsToPlayerPacket(sP.getId(), cooldown.getData()));
    }

    public void setCooldownTime(Cooldown cooldown, int duration) {
        this.active.put(cooldown, IntegerReference.create(duration));
    }
}
