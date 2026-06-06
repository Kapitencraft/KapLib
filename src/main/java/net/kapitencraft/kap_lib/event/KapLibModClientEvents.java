package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.attribute.ExtendedItemProperties;
import net.kapitencraft.kap_lib.inventory_page.page_renderer.InventoryPageRenderers;
import net.kapitencraft.kap_lib.item.modifier_display.ModifierDisplayManager;
import net.kapitencraft.kap_lib.particle.animation.core.ClientParticleAnimationManager;
import net.kapitencraft.kap_lib.particle.custom.DamageIndicatorParticle;
import net.kapitencraft.kap_lib.particle.custom.LightningParticle;
import net.kapitencraft.kap_lib.particle.custom.ShimmerShieldParticle;
import net.kapitencraft.kap_lib.particle.registry.ExtraParticleTypes;
import net.kapitencraft.kap_lib.shader.config.ShaderClientModConfig;
import net.kapitencraft.kap_lib.shader.event.custom.client.RegisterUniformsEvent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(Dist.CLIENT)
public class KapLibModClientEvents {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ExtraParticleTypes.DAMAGE_INDICATOR.get(), new DamageIndicatorParticle.Provider());
        event.registerSpecial(ExtraParticleTypes.LIGHTNING.get(), new LightningParticle.Provider());
        event.registerSprite(ExtraParticleTypes.SHIMMER_SHIELD.get(), new ShimmerShieldParticle.Provider());
    }

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

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent event) {
        ItemProperties.register(Items.BOW, ResourceLocation.withDefaultNamespace("pull"), ExtendedItemProperties.BOW_PULL);
        ModifierDisplayManager.init();
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        InventoryPageRenderers.init();
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ClientParticleAnimationManager.INSTANCE);
    }
}
