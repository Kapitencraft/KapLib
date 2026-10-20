package net.kapitencraft.kap_lib.shader.event.handler;

import net.kapitencraft.kap_lib.shader.ShaderModule;
import net.kapitencraft.kap_lib.shader.config.ShaderClientModConfig;
import net.kapitencraft.kap_lib.shader.event.custom.client.RegisterUniformsEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = ShaderModule.MODULE_ID)
public class ShaderClientEvents {

    @SubscribeEvent
    public static void registerUniforms(RegisterUniformsEvent event) {
        event.addVecUniform("ChromaConfig", () -> {
            float[] floats = new float[4];
            floats[0] = ShaderClientModConfig.getChromaOrigin().getConfigId();
            floats[1] = ShaderClientModConfig.getChromaSpacing();
            floats[2] = ShaderClientModConfig.getChromaSpeed();
            floats[3] = ShaderClientModConfig.getChromaType().getConfigId();
            return floats;
        });
    }
}
