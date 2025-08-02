package net.kapitencraft.kap_lib.io.network.S2C;

import net.kapitencraft.kap_lib.io.network.SimplePacket;
import net.kapitencraft.kap_lib.requirements.RequirementManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncRequirementsPacket implements SimplePacket {
    private final RequirementManager manager;

    public SyncRequirementsPacket(RequirementManager manager) {
        this.manager = manager;
    }

    public SyncRequirementsPacket(FriendlyByteBuf buf) {
        this.manager = new RequirementManager();
        this.manager.readFromNetwork(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        this.manager.toNetwork(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> sup) {
        sup.get().enqueueWork(()-> RequirementManager.instance = manager);
    }
}