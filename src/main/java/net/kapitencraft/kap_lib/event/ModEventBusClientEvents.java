package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.client.ModBEWLR;
import net.kapitencraft.kap_lib.client.particle.DamageIndicatorParticle;
import net.kapitencraft.kap_lib.client.particle.LightningParticle;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticle;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.event.custom.client.RegisterUniformsEvent;
import net.kapitencraft.kap_lib.inventory.page_renderer.InventoryPageRenderers;
import net.kapitencraft.kap_lib.item.modifier_display.ModifierDisplayManager;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
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
            floats[0] = ClientModConfig.getChromaOrigin().getConfigId();
            floats[1] = ClientModConfig.getChromaSpacing();
            floats[2] = ClientModConfig.getChromaSpeed();
            floats[3] = ClientModConfig.getChromaType().getConfigId();
            return floats;
        });
    }

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent event) {
        ItemProperties.register(Items.BOW, ResourceLocation.withDefaultNamespace("pull"), (stack, level, living, p_174679_) -> {
            if (living == null || living.getAttribute(ExtraAttributes.DRAW_SPEED) == null) {
                return 0.0F;
            } else {
                return living.getUseItem() != stack ? 0.0F : (float)((stack.getUseDuration(living) - living.getUseItemRemainingTicks()) / 20.0F  * living.getAttributeValue(ExtraAttributes.DRAW_SPEED) / 100);
            }
        });
        ModifierDisplayManager.init();
    }

    @SubscribeEvent
    public void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ModBEWLR.setInstance());
    }

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        InventoryPageRenderers.init();
    }

}
