package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CooldownStartedPacket implements SimplePacket {
    private final Cooldown cooldown;
    private final int duration, entityId;

    public CooldownStartedPacket(Cooldown cooldown, int duration, int entityId) {
        this.cooldown = cooldown;
        this.duration = duration;
        this.entityId = entityId;
    }

    public CooldownStartedPacket(FriendlyByteBuf buf) {
        this(buf.readRegistryIdUnsafe(ExtraRegistries.COOLDOWNS), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeRegistryIdUnsafe(ExtraRegistries.COOLDOWNS, cooldown);
        buf.writeInt(duration);
        buf.writeInt(entityId);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            Cooldowns.get((LivingEntity) Minecraft.getInstance().level.getEntity(entityId)).setCooldownTime(cooldown, duration);
        });
    }
}
