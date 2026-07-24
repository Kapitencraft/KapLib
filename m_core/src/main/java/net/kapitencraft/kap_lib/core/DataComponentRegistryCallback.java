package net.kapitencraft.kap_lib.core;

import net.kapitencraft.kap_lib.core.client.ItemComponentTooltipOrder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.callback.BakeCallback;

public class DataComponentRegistryCallback implements BakeCallback<DataComponentType<?>> {
    @Override
    public void onBake(Registry<DataComponentType<?>> registry) {
        ItemComponentTooltipOrder.refresh();
    }
}
