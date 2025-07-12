package net.kapitencraft.kap_lib.cooldown;


import net.kapitencraft.kap_lib.io.network.ModMessages;
import net.kapitencraft.kap_lib.io.network.S2C.capability.SyncCooldownsToPlayerPacket;
import net.kapitencraft.kap_lib.util.IntegerReference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import java.util.*;


public class Cooldowns {
    public static final Capability<Cooldowns> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private final LivingEntity entity;

    private final Map<Cooldown, IntegerReference> active = new HashMap<>();

    public Cooldowns(LivingEntity entity) {
        this.entity = entity;
    }

    public boolean isActive(Cooldown cooldown) {
        return active.containsKey(cooldown);
    }

    public void tick() {
        List<Cooldown> toRemove = new ArrayList<>();
        active.forEach((cooldown, integerReference) -> {
            integerReference.decrease();
            if (integerReference.getIntValue() <= 0) toRemove.add(cooldown);
        });
        toRemove.forEach(c -> {
            c.onDone(this.entity);
            active.remove(c);
        });
    }

    public void applyCooldown(Cooldown cooldown, boolean reduceWithTime) {
        int time = cooldown.getCooldownTime(this.entity, reduceWithTime);
        if (time > 0) this.active.put(cooldown, IntegerReference.create(time));
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
        return living.getCapability(Cooldowns.CAPABILITY).orElseThrow(() -> new NullPointerException("unable to get capability"));
    }


    public static void send(ServerPlayer sP) {
        Cooldowns cooldown = get(sP);
        ModMessages.sendToClientPlayer(new SyncCooldownsToPlayerPacket(sP.getId(), cooldown.getData()), sP);
    }

}
