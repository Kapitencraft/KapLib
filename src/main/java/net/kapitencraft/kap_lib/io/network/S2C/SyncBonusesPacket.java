package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.item.bonus.BonusManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBonusesPacket implements SimplePacket {
    private final BonusManager bonusManager;

    public SyncBonusesPacket(BonusManager bonusManager) {
        this.bonusManager = bonusManager;
    }

    public SyncBonusesPacket(FriendlyByteBuf buf) {
        this.bonusManager = BonusManager.fromNw(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.bonusManager.toNetwork(buf);
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(() -> BonusManager.instance = this.bonusManager);
        return false;
    }
}
