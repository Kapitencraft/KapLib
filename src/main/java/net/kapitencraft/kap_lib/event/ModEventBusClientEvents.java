package net.kapitencraft.kap_lib.event;

import net.kapitencraft.kap_lib.client.particle.DamageIndicatorParticle;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticle;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.event.custom.client.RegisterUniformsEvent;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ExtraParticleTypes.DAMAGE_INDICATOR.get(), new DamageIndicatorParticle.Provider());
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
        ItemProperties.register(Items.BOW, new ResourceLocation("pull"), (stack, level, living, p_174679_) -> {
            if (living == null || living.getAttribute(ExtraAttributes.DRAW_SPEED.get()) == null) {
                return 0.0F;
            } else {
                return living.getUseItem() != stack ? 0.0F : (float)((stack.getUseDuration() * living.getAttributeValue(ExtraAttributes.DRAW_SPEED.get()) / 100) - living.getUseItemRemainingTicks()) / 20.0F;
            }
        });
    }
}
