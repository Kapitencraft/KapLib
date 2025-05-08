package net.kapitencraft.kap_lib.event;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.ModBEWLR;
import net.kapitencraft.kap_lib.client.particle.DamageIndicatorParticle;
import net.kapitencraft.kap_lib.client.particle.LightningParticle;
import net.kapitencraft.kap_lib.client.particle.ShimmerShieldParticle;
import net.kapitencraft.kap_lib.config.ClientModConfig;
import net.kapitencraft.kap_lib.event.custom.client.RegisterItemModifiersDisplayExtensionsEvent;
import net.kapitencraft.kap_lib.event.custom.client.RegisterUniformsEvent;
import net.kapitencraft.kap_lib.helpers.AttributeHelper;
import net.kapitencraft.kap_lib.item.BaseAttributeUUIDs;
import net.kapitencraft.kap_lib.item.modifier_display.ItemModifiersDisplayExtension;
import net.kapitencraft.kap_lib.item.modifier_display.ModifierDisplayManager;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.ExtraParticleTypes;
import net.kapitencraft.kap_lib.registry.vanilla.VanillaTestItems;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
        ItemProperties.register(Items.BOW, new ResourceLocation("pull"), (stack, level, living, p_174679_) -> {
            if (living == null || living.getAttribute(ExtraAttributes.DRAW_SPEED.get()) == null) {
                return 0.0F;
            } else {
                return living.getUseItem() != stack ? 0.0F : (float)((stack.getUseDuration() - living.getUseItemRemainingTicks()) / 20.0F  * living.getAttributeValue(ExtraAttributes.DRAW_SPEED.get()) / 100);
            }
        });
        BaseAttributeUUIDs.init();
        ModifierDisplayManager.init();
    }

    @SubscribeEvent
    public void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ModBEWLR.setInstance());
    }

    @SubscribeEvent
    public static void onRegisterItemModifiersDisplayExtensions(RegisterItemModifiersDisplayExtensionsEvent event) {
        if (KapLibMod.DEBUG) event.register((living, stack) -> {
            if (stack.getItem() == VanillaTestItems.TEST_SWORD.get()) return new ItemModifiersDisplayExtension() {
                private static final HashMultimap<Attribute, AttributeModifier> modifiers = Util.make(HashMultimap.create(), m -> {
                    m.put(Attributes.ATTACK_DAMAGE, AttributeHelper.createModifier("TestDisplay", AttributeModifier.Operation.ADDITION, 20));
                });

                @Override
                public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot) {
                    return slot == EquipmentSlot.MAINHAND ? modifiers : null;
                }

                @Override
                public Style getStyle() {
                    return Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE);
                }

                @Override
                public Type getType() {
                    return Type.POINTY;
                }
            };
            return null;
        });
    }
}
