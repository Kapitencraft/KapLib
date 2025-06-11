package net.kapitencraft.kap_lib.client.overlay;

import net.kapitencraft.kap_lib.client.overlay.holder.MultiLineOverlay;
import net.kapitencraft.kap_lib.client.overlay.holder.SimpleOverlay;
import net.kapitencraft.kap_lib.helpers.MathHelper;
import net.kapitencraft.kap_lib.helpers.MiscHelper;
import net.kapitencraft.kap_lib.registry.ExtraAttributes;
import net.kapitencraft.kap_lib.registry.custom.Overlays;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OverlaysRegister {


    public static void register(OverlayManager manager) {
        manager.createRenderer(Overlays.STATS, properties -> new MultiLineOverlay(
                Component.translatable("overlay.stats"),
                properties,
                -10,
                List.of(
                        player -> Component.translatable("overlay.stats.protection", getDamageProtection(player)).withStyle(ChatFormatting.DARK_BLUE),
                        player -> Component.translatable("overlay.stats.ehp", MathHelper.defRound(player.getHealth() * 100 / (100 - getDamageProtection(player)))).withStyle(ChatFormatting.DARK_AQUA),
                        player -> Component.translatable("overlay.stats.speed", cancelGravityMovement(player)).withStyle(ChatFormatting.YELLOW)
                )
        ));
        manager.createRenderer(Overlays.MANA, properties -> new SimpleOverlay(
                Component.translatable("overlay.mana"),
                properties,
                player -> Component.translatable(
                        "overlay.mana.display",
                        MathHelper.shortRound(player.getAttributeValue(ExtraAttributes.MANA.get())),
                        MathHelper.shortRound(player.getPersistentData().getDouble(MiscHelper.OVERFLOW_MANA_ID)),
                        player.getAttributeValue(ExtraAttributes.MAX_MANA.get()),
                        MathHelper.defRound(player.getPersistentData().getDouble("manaRegen") * 20)
                ).withStyle(ChatFormatting.BLUE)
        ));
    }

    private static double getDamageProtection(LivingEntity living) {
        return MathHelper.defRound(100 - MathHelper.calculateDamage(100, living.getAttributeValue(Attributes.ARMOR), living.getAttributeValue(Attributes.ARMOR_TOUGHNESS)));
    }

    private static double cancelGravityMovement(LivingEntity living) {
        Vec3 delta = living.getDeltaMovement();
        if (living.onGround())
            delta = delta.with(Direction.Axis.Y, 0);
        return MathHelper.defRound(delta.length() * 20);
    }
}
