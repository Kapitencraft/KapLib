package net.kapitencraft.kap_lib.io.network.S2C.capability;

import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.cooldown.Cooldowns;
import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class SyncCooldownsToPlayerPacket implements SimplePacket {
    private final int playerId;
    private final Map<Cooldown, Integer> data;

    public SyncCooldownsToPlayerPacket(int playerId, Map<Cooldown, Integer> data) {
        this.playerId = playerId;
        this.data = data;
    }

    public SyncCooldownsToPlayerPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readInt();
        this.data = buf.readMap(buf1 -> buf1.readRegistryIdUnsafe(ExtraRegistries.COOLDOWNS), FriendlyByteBuf::readInt);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(playerId);
        buf.writeMap(this.data, (buf1, cooldown) -> buf1.writeRegistryIdUnsafe(ExtraRegistries.COOLDOWNS, cooldown), FriendlyByteBuf::writeInt);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level.getEntity(playerId) instanceof LivingEntity living) {
                Cooldowns.get(living).loadData(data);
            }
        });
    }
}
